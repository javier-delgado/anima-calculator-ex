package com.javierdelgado.anima_calculator_ex.domain

import android.content.Context
import com.javierdelgado.anima_calculator_ex.models.DiceRollConfig

class SettingsManager(private val context: Context) {
    fun save(
        allowOpenRoll: Boolean,
        allowFumble: Boolean,
        allowPalindrome: Boolean,
        openRollMinValue: String?,
        fumbleMaxValue: String?
    ) {
        val config = DiceRollConfig(
            openRollEnabled = allowOpenRoll,
            fumbleEnabled = allowFumble,
            palindromeEnabled = allowPalindrome,
            openRollMinValue = if (openRollMinValue.isNullOrBlank()) 90 else openRollMinValue.toInt(),
            fumbleMaxValue = if (fumbleMaxValue.isNullOrBlank()) 3 else fumbleMaxValue.toInt()
        )
        config.persist()
    }

}