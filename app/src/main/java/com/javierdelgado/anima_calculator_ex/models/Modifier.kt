package com.javierdelgado.anima_calculator_ex.models

import android.content.Context
import androidx.annotation.ArrayRes

data class Modifier(val name: String, val value: Int) {
    companion object {
        fun loadListFromResources(context: Context, @ArrayRes namesArrayRes: Int, @ArrayRes valuesArrayRes: Int): List<Modifier> {
            val names = context.resources.getStringArray(namesArrayRes)
            val values = context.resources.getIntArray(valuesArrayRes)
            val modifiers = mutableListOf<Modifier>()

            return names.mapIndexed { idx, name ->
                Modifier(
                    name = name,
                    value = values[idx]
                )
            }.sortedBy { it.name }
        }
    }

    override fun toString(): String {
        return "$name | $value"
    }
}
