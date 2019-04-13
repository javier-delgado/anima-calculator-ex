package com.javierdelgado.anima_calculator_ex.ui.initiative

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.javierdelgado.anima_calculator_ex.BuildConfig
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter
import com.javierdelgado.anima_calculator_ex.models.Party
import com.javierdelgado.anima_calculator_ex.snackbar
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import kotlinx.android.synthetic.main.fragment_initiative_calculator.*

class InitiativeCalculatorFragment : Fragment() {
    private val modals by lazy { InitiativeCalculatorModals(context!!) }
    private var loadedParty: Party? = null // This keeps the persisted party (if it is persisted)
    private lateinit var characters: MutableList<InitiativeCharacter>
    private val adapter by lazy { CharactersInitiativeAdapter(characters) }

    companion object {
        fun newInstance(): InitiativeCalculatorFragment {
            val fragment = InitiativeCalculatorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_initiative_calculator, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        txtVersion.text = getString(R.string.v_, BuildConfig.VERSION_NAME)
        val quickSavedParty = Party.loadQuickSaveParty()
        characters = quickSavedParty.characters?.toMutableList() ?: mutableListOf()
        if (quickSavedParty.persisted()) {
            loadedParty = quickSavedParty
            txtPartyName.apply {
                text = quickSavedParty.name
                visibility = View.VISIBLE
            }
        }
        setupCharacterList()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.initiative_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
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
        recCharactersInitiative.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recCharactersInitiative.isNestedScrollingEnabled = false
        adapter.sort()
        recCharactersInitiative.adapter = adapter
    }

    private fun saveNewParty(name: String) {
        loadedParty = Party().apply {
            this@InitiativeCalculatorFragment.characters.forEach { it.party = this }
            this.name = name
            this.characters = this@InitiativeCalculatorFragment.characters
            save()
        }
        txtPartyName.apply {
            text = name
            visibility = View.VISIBLE
        }
        view?.snackbar(R.string.party_saved)
    }

    private fun clearAll() {
        characters.clear()
        adapter.notifyDataSetChanged()
        loadedParty = null
        txtPartyName.visibility = View.GONE
    }

    private fun loadParty(party: Party) {
        loadedParty = party
        characters.clear()
        characters.addAll(party.characters?.toMutableList() ?: mutableListOf())
        adapter.notifyDataSetChanged()
        txtPartyName.apply {
            text = party.name
            visibility = View.VISIBLE
        }
    }

    private fun deleteLoadedParty() {
        loadedParty?.delete()
        clearAll()
    }

    private fun bindListeners() {
        btnAddPlayer.setOnClickListener {
            modals.showNewCharacterForm { character ->
                characters.add(character)
                adapter.notifyItemInserted(characters.size - 1)
            }
        }
        btnRollForInitiative.setOnClickListener {
            MaterialDialog(context!!).show {
                message(R.string.roll_initiative_confirm)
                positiveButton(R.string.do_roll) {
                    characters.forEach { it.rollForInitiative(getString(R.string.initiative_roll)) }
                    adapter.sort()
                    adapter.notifyDataSetChanged()
                }
                negativeButton(R.string.cancel)
            }
        }
        btnSort.setOnClickListener {
            adapter.sort()
            adapter.notifyDataSetChanged()
        }
    }

    private fun unbindListeners() {
        btnAddPlayer.setOnClickListener(null)
        btnRollForInitiative.setOnClickListener(null)
        btnSort.setOnClickListener(null)
    }

}
