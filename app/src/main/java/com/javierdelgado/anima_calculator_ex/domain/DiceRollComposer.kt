package com.javierdelgado.anima_calculator_ex.domain

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.models.DiceRoll

class DiceRollComposer(private val context: Context, private val roll: DiceRoll) {
    fun compose(): SpannableStringBuilder {
        val sp = SpannableStringBuilder("")

        setTag(sp)
        setFinalResult(sp)
        setFumble(sp)
        setOpenRollAndPalindrome(sp)
        setRollsList(sp)

        return sp
    }

    private fun setTag(sp: SpannableStringBuilder) {
        sp.append("${roll.tag} -")
    }

    private fun setFinalResult(sp: SpannableStringBuilder) {
        val finalResult = roll.finalResult.toString()
        sp.append(" ${context.getString(R.string.final_result_of)} $finalResult.")
        makeBold(sp, sp.length - 1 - finalResult.length, sp.length)
    }

    private fun setFumble(sp: SpannableStringBuilder) {
        if (!roll.didFumble()) return
        val fumbleLevel = roll.fumbleLevel.toString()
        sp.append(" ${context.getString(R.string.you_fumbled_with_confirmation_level_of)} $fumbleLevel.")
        makeBold(sp, sp.length - 1 - fumbleLevel.length, sp.length)
    }

    private fun setOpenRollAndPalindrome(sp: SpannableStringBuilder) {
        if (!roll.didOpenRoll()) return
        val openRollCount = roll.openRollCount.toString()
        sp.append(" $openRollCount")
        makeBold(sp, sp.length - 1 - openRollCount.length, sp.length)
        sp.append(" ${context.resources.getQuantityString(R.plurals.open_rolls, roll.openRollCount)}")

        if (roll.didPalindromeRoll()) {
            val palindromeCount = roll.confirmedPalindromeCount.toString()
            sp.append(", $palindromeCount")
            makeBold(sp, sp.length - 1 - palindromeCount.length, sp.length)
            sp.append(" ${context.resources.getQuantityString(R.plurals.palindromes, roll.confirmedPalindromeCount)}.")
        } else {
            sp.append(".")
        }
    }

    private fun setRollsList(sp: SpannableStringBuilder) {
        if (roll.results.size < 2) return
        sp.append(" ${context.getString(R.string.your_rolls)}:")
        roll.results.forEachIndexed { idx, result ->
            sp.append(" $result")
            makeBold(sp, sp.length - 1 - result.toString().length, sp.length)
            if (idx < roll.results.size -1) {  // Not last element
                sp.append(" ->")
            }
        }
    }

    private fun makeBold(sp: SpannableStringBuilder, start: Int, end: Int) {
        sp.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}
