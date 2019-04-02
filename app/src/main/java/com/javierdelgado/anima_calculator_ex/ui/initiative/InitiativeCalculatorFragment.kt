package com.javierdelgado.anima_calculator_ex.ui.initiative

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter
import kotlinx.android.synthetic.main.fragment_initiative_calculator.*

class InitiativeCalculatorFragment : Fragment() {
    private val modals by lazy { InitiativeCalculatorModals(context!!) }
    private val characters = mutableListOf<InitiativeCharacter>()
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
        setupCharacterList()
    }

    override fun onResume() {
        super.onResume()
        bindListeners()
    }

    override fun onPause() {
        super.onPause()
        unbindListeners()
    }

    private fun setupCharacterList() {
        recCharactersInitiative.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recCharactersInitiative.adapter = adapter
    }


    private fun bindListeners() {
        btnAddPlayer.setOnClickListener {
            modals.showNewCharacterForm { character ->
                characters.add(character)
                adapter.notifyItemInserted(characters.size-1)
            }
        }
        btnRollForInitiative.setOnClickListener {
            MaterialDialog(context!!).show {
                message(R.string.roll_initiative_confirm)
                positiveButton(R.string.roll) {
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
