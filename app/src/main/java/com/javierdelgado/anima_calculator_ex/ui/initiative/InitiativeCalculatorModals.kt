package com.javierdelgado.anima_calculator_ex.ui.initiative

import android.content.Context
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter
import com.javierdelgado.anima_calculator_ex.models.Party
import com.javierdelgado.anima_calculator_ex.models.Party_Table
import com.javierdelgado.anima_calculator_ex.showSoftKeyboard
import com.javierdelgado.anima_calculator_ex.utils.MathEvaluator
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.select


class InitiativeCalculatorModals(private val context: Context) {

    fun showNewCharacterForm(afterSave: (character: InitiativeCharacter) -> Unit = {}) {
        val dialog = MaterialDialog(context).show {
            title(R.string.new_character)
            customView(R.layout.modal_new_character)
            positiveButton(R.string.save) {
                val view = it.getCustomView()
                val name = view.findViewById<EditText>(R.id.edtName).text.toString()
                val baseInitiativeText = view.findViewById<EditText>(R.id.edtBaseInitiative).text.toString()


                val character = InitiativeCharacter(name, MathEvaluator.evaluate(baseInitiativeText))
                afterSave(character)
            }
            negativeButton(R.string.cancel)
        }
        setupDialog(dialog)
    }

    private fun setupDialog(dialog: MaterialDialog) {
        val view = dialog.getCustomView()
        val edtName = view.findViewById<EditText>(R.id.edtName)
        edtName.requestFocus()
        showSoftKeyboard(view.context)
    }

    fun showSavePartyForm(onSave: (text: String) -> Unit) {
        MaterialDialog(context).show {
            input(hintRes = R.string.party_name) { _, text ->
                onSave(text.toString())
            }
            positiveButton(R.string.save)
            negativeButton(R.string.cancel)
        }
    }

    fun showLoadParty(onLoad: (party: Party) -> Unit) {
        val parties = select.from(Party::class.java).queryList()
        MaterialDialog(context).show {
            listItems(items = parties.map { it.name }) { _, i, _ ->
                onLoad(parties[i])
            }
        }
    }

    fun showConfirmClear(onConfirm: () -> Unit) {
        MaterialDialog(context).show {
            message(R.string.clear_party_confirm)
            positiveButton(R.string.yes) {
                onConfirm()
            }
            negativeButton(R.string.no)
        }
    }
}
