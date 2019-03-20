package com.javierdelgado.anima_calculator_ex.domain

import android.content.Context
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.models.Combat

class CombatResultComposer(private val context: Context, private val combat: Combat) {
    var mainText: String = ""
    var secondaryText: String = ""

    fun composeText() {
        when {
            combat.result() == 0 -> {
                noCombatResult()
            }
            combat.result() < 0 -> {
                val counterAttackBonus = - combat.result() / 10 * 5
                counterAttackResult(counterAttackBonus)
            }
            else -> {
                val absDifference: Int = combat.result() - (20 + combat.ATValue * 10)
                val percentage: Int = absDifference / 10 * 10
                val damageDealt: Int = Math.ceil(percentage.toDouble() * combat.finalDamage / 100).toInt()

                if (percentage > 0)
                    attackWinsResult(percentage, damageDealt)
                else
                    defenseWinsResult()
            }
        }
    }

    private fun noCombatResult() {
        mainText = context.getString(R.string.nothing_happens)
        secondaryText = ""
    }

    private fun defenseWinsResult() {
        mainText = context.getString(R.string.defensive)
    }

    private fun attackWinsResult(percentage: Int, damageDealt: Int) {
        mainText = context.getString(R.string.damage_dealt_, damageDealt)
        secondaryText = context.getString(R.string.percentage_out_of_total_damage, percentage, combat.finalDamage)
    }

    private fun counterAttackResult(counterAttackBonus: Int) {
        mainText = if(counterAttackBonus > 0)
            context.getString(R.string.counterattack_with_bonus, counterAttackBonus)
        else
            context.getString(R.string.counterattack_without_bonus)
        secondaryText = ""
    }


}