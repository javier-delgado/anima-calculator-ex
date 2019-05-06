package com.javierdelgado.anima_calculator_ex.ui.initiative

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.createSimpleTextWatcher
import com.javierdelgado.anima_calculator_ex.inflate
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter
import com.javierdelgado.anima_calculator_ex.showDiceRollSnackbar
import com.javierdelgado.anima_calculator_ex.utils.MathEvaluator
import com.raizlabs.android.dbflow.kotlinextensions.delete
import java.util.*


class CharactersInitiativeAdapter(private val characters: MutableList<InitiativeCharacter>) :
    RecyclerView.Adapter<CharacterInitiativeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterInitiativeViewHolder {
        val inflatedView = parent.inflate(R.layout.item_character_initiative, false)
        return CharacterInitiativeViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return characters.size
    }

    override fun onBindViewHolder(holder: CharacterInitiativeViewHolder, position: Int) {
        holder.bind(characters, position) { character ->
            character.delete()
            characters.remove(character)
            notifyDataSetChanged()
        }
    }

    override fun onViewDetachedFromWindow(holder: CharacterInitiativeViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }

    override fun onViewAttachedToWindow(holder: CharacterInitiativeViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    fun sort() {
        characters.sortByDescending { it.totalInitiative() }
    }
}

class CharacterInitiativeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Observer {
    private val txtCharacterName by lazy { itemView.findViewById<TextView>(R.id.txtCharacterName) }
    private val txtPosition by lazy { itemView.findViewById<TextView>(R.id.txtPosition) }
    private val txtInitiative by lazy { itemView.findViewById<TextView>(R.id.txtInitiative) }
    private val lytInitiativeHeader by lazy { itemView.findViewById<ViewGroup>(R.id.lytInitiativeHeader) }
    private val edtBaseInitiative by lazy { itemView.findViewById<EditText>(R.id.edtBaseInitiative) }
    private val edtInitiativeRoll by lazy { itemView.findViewById<EditText>(R.id.edtInitiativeRoll) }
    private val chkIsEnemy by lazy { itemView.findViewById<CheckBox>(R.id.chkIsEnemy) }
    private val chkUroboros by lazy { itemView.findViewById<CheckBox>(R.id.chkUroboros) }
    private val edtFumble by lazy { itemView.findViewById<EditText>(R.id.edtFumble) }
    private val btnRollInitiativeDice by lazy { itemView.findViewById<ImageButton>(R.id.btnRollInitiativeDice) }
    private val groupInitiativeSettings by lazy { itemView.findViewById<Group>(R.id.groupInitiativeSettings) }
    private val txtSurpriseCharacters by lazy { itemView.findViewById<TextView>(R.id.txtSurpriseCharacters) }
    private val imgSurprise by lazy { itemView.findViewById<ImageView>(R.id.imgSurprise) }

    private lateinit var others: List<InitiativeCharacter>
    private lateinit var character: InitiativeCharacter
    private var deleteCallback: (InitiativeCharacter) -> Unit = {}

    fun bind(characters: MutableList<InitiativeCharacter>, position: Int, onCharacterDelete: (InitiativeCharacter) -> Unit) {
        character = characters[position]
        others = characters.filterIndexed { index, _-> position != index }
        deleteCallback = onCharacterDelete
        edtBaseInitiative.setText(character.base.toString())
        edtInitiativeRoll.setText(character.roll.toString())
        edtFumble.setText(character.fumble.toString())
        chkIsEnemy.isChecked = character.enemy
        chkUroboros.isChecked = character.uroboros
        updateFormVisibility()

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
        lytInitiativeHeader.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.initiative_header_2))
        setSurprisedCharactersText()
    }

    private fun setSurprisedCharactersText() {
        val surprisedBy = character.isSurprisedBy(others)
        val surprises = character.surprises(others)

        if (surprisedBy.isEmpty() && surprises.isEmpty()) {
            txtSurpriseCharacters.text = ""
            imgSurprise.visibility = View.GONE
        } else {
            txtSurpriseCharacters.visibility = View.VISIBLE
            if (surprises.any()) {
                setSurpriseIcon(R.drawable.ic_up_arrow, R.color.colorSurprise)
            } else {
                setSurpriseIcon(R.drawable.ic_down_arrow, R.color.colorSurprised)
            }

            val text = StringBuilder()
            surprises.forEach { text.append(itemView.context.getString(R.string.surprises_, it.name)).append("\n") }
            surprisedBy.forEach { text.append(itemView.context.getString(R.string.surprised_by_, it.name)).append("\n") }
            txtSurpriseCharacters.text = text.trim()
        }

    }

    private fun setSurpriseIcon(@DrawableRes resource: Int, @ColorRes colorRes: Int) {
        val drw = ContextCompat.getDrawable(itemView.context, resource)
        drw?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(itemView.context, colorRes), PorterDuff.Mode.SRC_IN)
        imgSurprise.setImageDrawable(drw)
        imgSurprise.visibility = View.VISIBLE
    }

    private fun bindButtons() {
        btnRollInitiativeDice.setOnClickListener {
            rollInitiative()
        }
        lytInitiativeHeader.setOnClickListener {
            character.dataVisible = !character.dataVisible
            updateFormVisibility()
        }
        lytInitiativeHeader.setOnLongClickListener {
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog(): Boolean {
        MaterialDialog(itemView.context).show {
            message(R.string.delete_character_confirmation)
            positiveButton(R.string.delete) {
                deleteCallback(character)
            }
            negativeButton(R.string.cancel)
        }
        return true
    }

    private fun updateFormVisibility() {
        groupInitiativeSettings.visibility = if(character.dataVisible) View.VISIBLE else View.GONE
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
        chkIsEnemy.setOnCheckedChangeListener { _, b ->
            character.enemy = b
        }
        chkUroboros.setOnCheckedChangeListener { _, b ->
            character.uroboros = b
        }
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
