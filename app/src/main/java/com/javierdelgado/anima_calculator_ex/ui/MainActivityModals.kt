package com.javierdelgado.anima_calculator_ex.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getListAdapter
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.inflate
import com.javierdelgado.anima_calculator_ex.models.Modifier

class MainActivityModals(private val context: Context) {
    private val attackModifiers by lazy { Modifier.loadListFromResources(context, R.array.attackModifiers, R.array.attackModifierValues) }
    private val defenseModifiers by lazy { Modifier.loadListFromResources(context, R.array.defenseModifiers, R.array.defenseModifierValues) }

    fun showAttackModifiers(selectedModifiers: List<Modifier>, callback: (List<Modifier>) -> Unit) {
        MaterialDialog(context).show {
            title(R.string.attack_modifiers)
            customListAdapter(ModifiersAdapter(attackModifiers, selectedModifiers))
            positiveButton(R.string.ok) { dialog ->
                val adapter = dialog.getListAdapter() as ModifiersAdapter
                callback(adapter.getSelectedModifiers())
            }
        }
    }

    fun showDefenseModifiers(selectedModifiers: List<Modifier>, callback: (List<Modifier>) -> Unit) {
        MaterialDialog(context).show {
            title(R.string.defense_modifiers)
            customListAdapter(ModifiersAdapter(defenseModifiers, selectedModifiers))
            positiveButton(R.string.ok) { dialog ->
                val adapter = dialog.getListAdapter() as ModifiersAdapter
                callback(adapter.getSelectedModifiers())
            }
        }
    }

    fun showATSelector(callback: (Int) -> Unit) {
        MaterialDialog(context).show {
            title(R.string.at)
            listItemsSingleChoice(R.array.AT) { _, idx, _ ->
                callback(idx)
            }
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

}

class ModifierViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val chkModifier by lazy { itemView.findViewById<CheckBox>(R.id.chkModifier) }

    fun bind(sModifier: SelectableModifier) {
        chkModifier.setOnCheckedChangeListener(null)
        chkModifier.text = sModifier.modifier.toString()
        chkModifier.isChecked = sModifier.selected
        chkModifier.setOnCheckedChangeListener { _, b ->
            sModifier.selected = b }
    }
}

data class SelectableModifier(val modifier: Modifier, var selected: Boolean)