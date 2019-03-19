package com.javierdelgado.anima_calculator_ex.performers

import com.javierdelgado.anima_calculator_ex.models.DiceRoll
import com.javierdelgado.anima_calculator_ex.models.DiceRollConfig


class DiceRoller(var defaultRollConfig: DiceRollConfig = DiceRollConfig()) {
    private var roll = DiceRoll()

    fun perform(): DiceRoll {
        roll = DiceRoll()
        doRoll()
        return roll
    }

    private fun doRoll(rollConfig: DiceRollConfig = defaultRollConfig) {
        val diceResult = (1..100).random()
        roll.result += diceResult

        if (rollConfig.fumbleEnabled && diceResult <= rollConfig.fumbleMaxValue) {
            roll.fumbleLevel = (1..100).random()
            return
        }

        if(rollConfig.openRollEnabled && diceResult >= rollConfig.openRollMinValue) {
            roll.openRollCount++

            // If it's open, we roll again with fumble disabled
            val newOpenRollMinValue = if (rollConfig.openRollMinValue < 100) (rollConfig.openRollMinValue + 1) else 100
            val newRollConfig = rollConfig.copy(fumbleEnabled = false, openRollMinValue = newOpenRollMinValue)

            doRoll(newRollConfig)
        }
    }
}

//if (rollConfig.palindromeEnabled && diceResult.isPalindrome()) {
//    val c = r.nextInt(10) + 1
//    if (c == diceResult % 10) {
//        diceResult = 100
//    } else {
//    }
//}