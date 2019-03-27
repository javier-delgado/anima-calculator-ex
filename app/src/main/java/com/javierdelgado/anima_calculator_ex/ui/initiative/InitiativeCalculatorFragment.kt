package com.javierdelgado.anima_calculator_ex.ui.initiative

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.inflate
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter
import com.javierdelgado.anima_calculator_ex.ui.combat.CombatCalculatorModals
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
    }

    private fun unbindListeners() {
        btnAddPlayer.setOnClickListener(null)
        btnRollForInitiative.setOnClickListener(null)
    }

}

class CharactersInitiativeAdapter(private val characters: List<InitiativeCharacter>) :
    RecyclerView.Adapter<CharacterInitiativeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterInitiativeViewHolder {
        val inflatedView = parent.inflate(R.layout.item_character_initiative, false)
        return CharacterInitiativeViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    override fun onBindViewHolder(holder: CharacterInitiativeViewHolder, position: Int) {
        holder.bind(characters.get(position))
    }
}

class CharacterInitiativeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val txtCharacterName by lazy { itemView.findViewById<TextView>(R.id.txtCharacterName) }
    private val lytInitiativeHeader by lazy { itemView.findViewById<ViewGroup>(R.id.lytInitiativeHeader) }

    fun bind(character: InitiativeCharacter) {
        txtCharacterName.text = character.name
        lytInitiativeHeader.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.material_grey600))
    }
}