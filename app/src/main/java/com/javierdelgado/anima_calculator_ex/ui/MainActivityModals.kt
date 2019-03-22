package com.javierdelgado.anima_calculator_ex.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getListAdapter
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.domain.SettingsManager
import com.javierdelgado.anima_calculator_ex.inflate
import com.javierdelgado.anima_calculator_ex.models.DiceRollConfig
import com.javierdelgado.anima_calculator_ex.models.Modifier

class MainActivityModals(private val context: Context) {
    private val attackModifiers by lazy { Modifier.loadListFromResources(context, R.array.attackModifiers, R.array.attackModifierValues) }
    private val defenseModifiers by lazy { Modifier.loadListFromResources(context, R.array.defenseModifiers, R.array.defenseModifierValues) }

    fun showAttackModifiers(selectedModifiers: List<Modifier>, callback: (List<Modifier>) -> Unit) {
        showModifiersDialog(R.string.attack_modifiers, attackModifiers, selectedModifiers, callback)
    }

    fun showDefenseModifiers(selectedModifiers: List<Modifier>, callback: (List<Modifier>) -> Unit) {
        showModifiersDialog(R.string.defense_modifiers, defenseModifiers, selectedModifiers, callback)
    }

    fun showATSelector(callback: (Int) -> Unit) {
        MaterialDialog(context).show {
            title(R.string.at)
            listItemsSingleChoice(R.array.AT) { _, idx, _ ->
                callback(idx)
            }
        }
    }

    fun showSettings(afterSave: () -> Unit = {}) {
        val dialog = MaterialDialog(context).show {
            title(R.string.settings)
            customView(R.layout.modal_settings)
            positiveButton(R.string.save) {
                val view = it.getCustomView()
                val allowOpenRoll = view.findViewById<CheckBox>(R.id.chkOpenRoll).isChecked
                val allowFumble = view.findViewById<CheckBox>(R.id.chkFumble).isChecked
                val allowPalindrome = view.findViewById<CheckBox>(R.id.chkPalindrome).isChecked
                val openRollMinValue = view.findViewById<EditText>(R.id.edtOpenRollMinValue).text
                val fumbleMaxValue = view.findViewById<EditText>(R.id.edtFumbleMaxvalue).text
                SettingsManager(context).save(
                    allowOpenRoll,
                    allowFumble,
                    allowPalindrome,
                    openRollMinValue.toString(),
                    fumbleMaxValue.toString()
                )
                afterSave()
            }
        }
        setupSettingsDialog(dialog)
    }

    private fun showModifiersDialog(
        @StringRes titleRes: Int,
        modifiers: List<Modifier>,
        selectedModifiers: List<Modifier>,
        callback: (List<Modifier>) -> Unit
    ) {
        MaterialDialog(context).show {
            title(titleRes)
            noAutoDismiss()
            customListAdapter(ModifiersAdapter(modifiers, selectedModifiers))
            positiveButton(R.string.ok) { dialog ->
                val adapter = dialog.getListAdapter() as ModifiersAdapter
                callback(adapter.getSelectedModifiers())
                dialog.dismiss()
            }
            negativeButton(R.string.deselect_all) { dialog ->
                val adapter = dialog.getListAdapter() as ModifiersAdapter
                adapter.deselectAll()
            }
        }
    }

    private fun setupSettingsDialog(dialog: MaterialDialog) {
        DiceRollConfig.loadAsync {
            val view = dialog.getCustomView()
            view.findViewById<CheckBox>(R.id.chkOpenRoll).isChecked = it.openRollEnabled
            view.findViewById<CheckBox>(R.id.chkFumble).isChecked = it.fumbleEnabled
            view.findViewById<CheckBox>(R.id.chkPalindrome).isChecked = it.palindromeEnabled
            view.findViewById<EditText>(R.id.edtOpenRollMinValue).setText(it.openRollMinValue.toString())
            view.findViewById<EditText>(R.id.edtFumbleMaxvalue).setText(it.fumbleMaxValue.toString())
        }
    }
}

class ModifiersAdapter(modifiers: List<Modifier>, selectedModifiers: List<Modifier>) : RecyclerView.Adapter<ModifierViewHolder>() {
    private var sModifiers: List<SelectableModifier>

    init {
        sModifiers = modifiers.map { modifier -> SelectableModifier(modifier, selectedModifiers.contains(modifier)) }
    }

    override fun getItemCount(): Int {
        return sModifiers.size
    }

    override fun onBindViewHolder(holder: ModifierViewHolder, position: Int) {
        holder.bind(sModifiers[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModifierViewHolder {
        val inflatedView = parent.inflate(R.layout.item_modifier, false)
        return ModifierViewHolder(inflatedView)
    }

    fun getSelectedModifiers(): List<Modifier> {
        return sModifiers.filter { it.selected }.map { it.modifier }
    }

    fun deselectAll() {
        sModifiers.forEachIndexed { index, sModifier ->
            if (sModifier.selected) {
                sModifier.selected = false
                notifyItemChanged(index)
            }
        }
    }
}

class ModifierViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val chkModifier by lazy { itemView.findViewById<CheckBox>(R.id.chkModifier) }

    fun bind(sModifier: SelectableModifier) {
        chkModifier.setOnCheckedChangeListener(null)
        chkModifier.text = sModifier.modifier.toSpannable(itemView.context)
        chkModifier.isChecked = sModifier.selected
        chkModifier.setOnCheckedChangeListener { _, b ->
            sModifier.selected = b }
    }
}

data class SelectableModifier(val modifier: Modifier, var selected: Boolean)