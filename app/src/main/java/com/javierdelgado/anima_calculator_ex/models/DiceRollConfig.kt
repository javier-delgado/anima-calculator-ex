package com.javierdelgado.anima_calculator_ex.models

import io.paperdb.Paper
import org.jetbrains.anko.doAsync

data class DiceRollConfig(var openRollEnabled: Boolean = true,
                          var fumbleEnabled: Boolean = true,
                          var palindromeEnabled: Boolean = false,
                          var fumbleMaxValue: Int = 3,
                          var openRollMinValue: Int = 90) {
    companion object {
        const val DICE_CONFIG_KEY = "dice_config"

        fun loadAsync(onLoad: (DiceRollConfig) -> Unit) {
            onLoad(loadSync())
        }

        fun loadSync(): DiceRollConfig {
            return Paper.book().read<DiceRollConfig>(DICE_CONFIG_KEY, DiceRollConfig())
        }
    }

    fun persist() {
        doAsync {
            Paper.book().write(DICE_CONFIG_KEY, this@DiceRollConfig)
        }
    }
}
