package com.javierdelgado.anima_calculator_ex.domain

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.models.Combat
import com.javierdelgado.anima_calculator_ex.models.Modifier

class CombatResultComposer(private val context: Context, private val combat: Combat) {
    var mainText: String = ""
    var secondaryText: String = ""
    var totalAttackText: String = ""
    var totalDefenseText: String = ""
    var totalAttackModifierText: SpannableString = SpannableString("")
    var totalDefenseModifierText: SpannableString = SpannableString("")

    fun composeText() {
        val result = combat.result()
        when {
            result == 0 -> {
                noCombatResult()
            }
            result < 0 -> {
                val counterAttackBonus = - result / 10 * 5
                counterAttackResult(counterAttackBonus)
            }
            else -> {
                val percentage: Int = combat.calculateDamagePercentage()
                val damageDealt: Int = combat.calculateDamageDealt()

                if (percentage > 0)
                    attackWinsResult(percentage, damageDealt)
                else
                    defenseWinsResult()
            }
        }
        totalAttackText = combat.totalAttack().toString()
        totalDefenseText = combat.totalDefense().toString()


        totalAttackModifierText = composeModifiersText(combat.selectedAttackModifiers)
        totalDefenseModifierText = composeModifiersText(combat.selectedDefenseModifiers)
    }

    private fun composeModifiersText(modifiers: List<Modifier>): SpannableString {
        val base = context.getString(R.string.edit_modifiers)
        val modifier = calculateModifier(modifiers)
        val sp = SpannableString("$base $modifier")
        sp.setSpan(TypefaceSpan("sans-serif-light"), base.length+1, sp.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return sp
    }

    private fun calculateModifier(modifiers: List<Modifier>): String {
        val result = modifiers.sumBy { it.value }
        return when {
            result > 0 -> "+$result"
            result < 0 -> result.toString()
            else -> ""
        }
    }

    private fun noCombatResult() {
        mainText = context.getString(R.string.nothing_happens)
        secondaryText = ""
    }

    private fun defenseWinsResult() {
        mainText = context.getString(R.string.defensive)
        secondaryText = ""
    }

    private fun attackWinsResult(percentage: Int, damageDealt: Int) {
        mainText = context.getString(R.string.damage_dealt_, damageDealt)
        secondaryText = context.getString(R.string.percentage_out_of_total_damage, percentage, combat.characterDamage)
    }

    private fun counterAttackResult(counterAttackBonus: Int) {
        mainText = if(counterAttackBonus > 0)
            context.getString(R.string.counterattack_with_bonus, counterAttackBonus)
        else
            context.getString(R.string.counterattack_without_bonus)
        secondaryText = ""
    }


}