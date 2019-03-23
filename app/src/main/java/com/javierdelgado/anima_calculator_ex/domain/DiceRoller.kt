package com.javierdelgado.anima_calculator_ex.domain

import android.util.Log
import com.javierdelgado.anima_calculator_ex.isPalindrome
import com.javierdelgado.anima_calculator_ex.models.DiceRoll
import com.javierdelgado.anima_calculator_ex.models.DiceRollConfig


class DiceRoller(private var defaultRollConfig: DiceRollConfig = DiceRollConfig.loadSync()) {
    private var roll = DiceRoll()

    fun perform(): DiceRoll {
        roll = DiceRoll()
        doRoll()
        return roll
    }

    private fun doRoll(rollConfig: DiceRollConfig = defaultRollConfig) {
        var diceResult = random()

        if (rollConfig.fumbleEnabled && diceResult <= rollConfig.fumbleMaxValue) {
            roll.fumbleLevel = random()
            roll.results.add(diceResult)
            roll.finalResult += diceResult
            return
        }

        if (rollConfig.palindromeEnabled && diceResult > 9 && diceResult.isPalindrome()) {
            val confirmationRollValue = random()
            if (confirmationRollValue.isPalindrome()) {
                roll.confirmedPalindromeCount++
                diceResult = 100
            }
        }

        roll.results.add(diceResult)
        roll.finalResult += diceResult

        if(rollConfig.openRollEnabled && diceResult >= rollConfig.openRollMinValue) {
            roll.openRollCount++

            // If it's open, we roll again with fumble disabled
            val newOpenRollMinValue = if (rollConfig.openRollMinValue < 100) (rollConfig.openRollMinValue + 1) else 100
            val newRollConfig = rollConfig.copy(fumbleEnabled = false, openRollMinValue = newOpenRollMinValue)

            doRoll(newRollConfig)
        }
    }

    private fun random(): Int {
        return (1..100).random()
    }
}
