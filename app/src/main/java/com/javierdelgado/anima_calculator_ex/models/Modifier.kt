package com.javierdelgado.anima_calculator_ex.models

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ArrayRes
import androidx.core.content.ContextCompat
import com.javierdelgado.anima_calculator_ex.R

data class Modifier(val name: String, val value: Int) {
    companion object {
        fun loadListFromResources(context: Context, @ArrayRes namesArrayRes: Int, @ArrayRes valuesArrayRes: Int): List<Modifier> {
            val names = context.resources.getStringArray(namesArrayRes)
            val values = context.resources.getIntArray(valuesArrayRes)

            return names.mapIndexed { idx, name ->
                Modifier(
                    name = name,
                    value = values[idx]
                )
            }.sortedBy { it.name }
        }
    }

    fun toSpannable(context: Context): SpannableString {
        val span = SpannableString(toString())
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.material_grey600)),
            name.length,
            span.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return span
    }

    private fun signedValue(): String {
        return if (value > 0) {
            "+$value"
        } else {
            value.toString()
        }
    }

    override fun toString(): String {
        return "$name ${signedValue()}"
    }
}
