package com.javierdelgado.anima_calculator_ex.ui.initiative

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dbflow5.structure.save
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.createSimpleTextWatcher
import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import com.javierdelgado.anima_calculator_ex.inflate
import com.javierdelgado.anima_calculator_ex.models.DiceRoll
import com.javierdelgado.anima_calculator_ex.models.DiceRollConfig
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter
import com.javierdelgado.anima_calculator_ex.showDiceRollSnackbar
import com.javierdelgado.anima_calculator_ex.utils.MathEvaluator
import org.jetbrains.anko.doAsync
import java.util.*


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

    override fun onViewDetachedFromWindow(holder: CharacterInitiativeViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }

    override fun onViewAttachedToWindow(holder: CharacterInitiativeViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }
}

class CharacterInitiativeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Observer {
    private val txtCharacterName by lazy { itemView.findViewById<TextView>(R.id.txtCharacterName) }
    private val txtPosition by lazy { itemView.findViewById<TextView>(R.id.txtPosition) }
    private val txtInitiative by lazy { itemView.findViewById<TextView>(R.id.txtInitiative) }
    private val lytInitiativeHeader by lazy { itemView.findViewById<ViewGroup>(R.id.lytInitiativeHeader) }
    private val edtBaseInitiative by lazy { itemView.findViewById<EditText>(R.id.edtBaseInitiative) }
    private val edtInitiativeRoll by lazy { itemView.findViewById<EditText>(R.id.edtInitiativeRoll) }
    private val edtFumble by lazy { itemView.findViewById<EditText>(R.id.edtFumble) }
    private val btnRollInitiativeDice by lazy { itemView.findViewById<ImageButton>(R.id.btnRollInitiativeDice) }

    private lateinit var character: InitiativeCharacter

    fun bind(char: InitiativeCharacter) {
        character = char
        edtBaseInitiative.setText(character.base.toString())
        edtInitiativeRoll.setText(character.roll.toString())
        edtFumble.setText(character.fumble.toString())
        updateUI()
        bindButtons()
        bindWatchers()
    }

    fun onDetach() {
        character.deleteObserver(this)
    }

    fun onAttach() {
        character.addObserver(this)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        txtCharacterName.text = character.name
        txtPosition.text = "${adapterPosition + 1}."
        txtInitiative.text = character.totalInitiative().toString()
        lytInitiativeHeader.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.material_grey600))
    }

    private fun bindButtons() {
        btnRollInitiativeDice.setOnClickListener {
            rollInitiative()
        }
    }

    private fun rollInitiative() {
        val diceRoll = character.rollForInitiative(itemView.context.getString(R.string.initiative_roll))
        edtInitiativeRoll.setText(character.roll.toString())
        edtFumble.setText(character.fumble.toString())

        showDiceRollSnackbar(diceRoll, itemView)
    }

    private fun bindWatchers() {
        edtBaseInitiative.addTextChangedListener(baseInitiativeTextWatcher)
        edtInitiativeRoll.addTextChangedListener(initiativeRollTextWatcher)
        edtFumble.addTextChangedListener(fumbleTextWatcher)
    }

    // When character data changes
    override fun update(p0: Observable?, p1: Any?) {
        updateUI()
    }

    private val baseInitiativeTextWatcher by lazy {
        createSimpleTextWatcher {
            character.base = if (edtBaseInitiative.text.isNotEmpty())
                MathEvaluator.evaluate(edtBaseInitiative.text.toString())
            else
                0
        }
    }

    private val initiativeRollTextWatcher by lazy {
        createSimpleTextWatcher {
            character.roll = if (edtInitiativeRoll.text.isNotEmpty())
                MathEvaluator.evaluate(edtInitiativeRoll.text.toString())
            else
                0
        }
    }

    private val fumbleTextWatcher by lazy {
        createSimpleTextWatcher {
            character.fumble = if (edtFumble.text.isNotEmpty())
                MathEvaluator.evaluate(edtFumble.text.toString())
            else
                0
        }
    }
}
