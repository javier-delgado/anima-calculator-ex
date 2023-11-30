package com.javierdelgado.anima_calculator_ex.ui.initiative

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.javierdelgado.anima_calculator_ex.BuildConfig
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.databinding.FragmentCombatCalculatorBinding
import com.javierdelgado.anima_calculator_ex.databinding.FragmentInitiativeCalculatorBinding
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter
import com.javierdelgado.anima_calculator_ex.models.Party
import com.javierdelgado.anima_calculator_ex.snackbar
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save

class InitiativeCalculatorFragment : Fragment() {
    private val modals by lazy { InitiativeCalculatorModals(requireContext()) }
    private var loadedParty: Party? = null // This keeps the persisted party (if it is persisted)
    private lateinit var characters: MutableList<InitiativeCharacter>
    private var charactersCopyForUndo: List<InitiativeCharacter>? = null
    private val adapter by lazy { CharactersInitiativeAdapter(characters) }
    private var _binding: FragmentInitiativeCalculatorBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): InitiativeCalculatorFragment {
            val fragment = InitiativeCalculatorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentInitiativeCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtVersion.text = getString(R.string.v_, BuildConfig.VERSION_NAME)
        val quickSavedParty = Party.loadQuickSaveParty()
        characters = quickSavedParty.characters?.toMutableList() ?: mutableListOf()
        if (quickSavedParty.persisted()) {
            loadedParty = quickSavedParty
            binding.txtPartyName.apply {
                text = quickSavedParty.name
                visibility = View.VISIBLE
            }
        }
        setupCharacterList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.initiative_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSaveParty -> modals.showSavePartyForm(loadedParty?.name ?: "") { name ->
                saveNewParty(name)
            }
            R.id.menuLoadParty -> modals.showLoadParty { party ->
                loadParty(party)
            }
            R.id.menuNewParty -> modals.showConfirmClear {
                clearAll()
            }
            R.id.menuDeleteParty -> modals.showDeleteParty {
                deleteLoadedParty()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        bindListeners()
    }

    override fun onPause() {
        super.onPause()
        unbindListeners()
        quickSave()
    }

    private fun quickSave() {
        Party(characters).apply {
            id = loadedParty?.id ?: 0
            name = loadedParty?.name ?: ""
            quickSave()
        }
    }

    private fun setupCharacterList() {
        binding.recCharactersInitiative.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recCharactersInitiative.isNestedScrollingEnabled = false
        adapter.sort()
        binding.recCharactersInitiative.adapter = adapter
    }

    private fun saveNewParty(name: String) {
        loadedParty = Party().apply {
            this@InitiativeCalculatorFragment.characters.forEach { it.party = this }
            this.name = name
            this.characters = this@InitiativeCalculatorFragment.characters
            save()
        }
        binding.txtPartyName.apply {
            text = name
            visibility = View.VISIBLE
        }
        view?.snackbar(R.string.party_saved)
    }

    private fun clearAll() {
        characters.clear()
        adapter.notifyDataSetChanged()
        loadedParty = null
        binding.txtPartyName.visibility = View.GONE
    }

    private fun loadParty(party: Party) {
        loadedParty = party
        characters.clear()
        characters.addAll(party.characters?.toMutableList() ?: mutableListOf())
        adapter.notifyDataSetChanged()
        binding.txtPartyName.apply {
            text = party.name
            visibility = View.VISIBLE
        }
    }

    private fun deleteLoadedParty() {
        loadedParty?.delete()
        clearAll()
    }

    private fun bindListeners() {
        binding.btnAddPlayer.setOnClickListener {
            modals.showNewCharacterForm { character ->
                characters.add(character)
                adapter.notifyItemInserted(characters.size - 1)
            }
        }
        binding.btnRollForInitiative.setOnClickListener {
            rollInitiativeForAll()
        }
        binding.btnSort.setOnClickListener {
            adapter.sort()
            adapter.notifyDataSetChanged()
        }
    }

    private fun rollInitiativeForAll() {
        charactersCopyForUndo = characters.map { it.copy() }
        characters.forEach { it.rollForInitiative(getString(R.string.initiative_roll)) }
        adapter.sort()
        adapter.notifyDataSetChanged()
        showUndoSnackBar()
    }

    private fun showUndoSnackBar() {
        Snackbar.make(view!!, R.string.initiative_rolled, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                charactersCopyForUndo?.let {
                    characters.clear()
                    characters.addAll(it)
                    adapter.sort()
                    adapter.notifyDataSetChanged()
                }
            }
            .show()
    }

    private fun unbindListeners() {
        binding.btnAddPlayer.setOnClickListener(null)
        binding.btnRollForInitiative.setOnClickListener(null)
        binding.btnSort.setOnClickListener(null)
    }

}
