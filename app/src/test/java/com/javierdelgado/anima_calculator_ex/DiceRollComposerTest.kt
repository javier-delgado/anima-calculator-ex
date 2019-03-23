package com.javierdelgado.anima_calculator_ex

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.javierdelgado.anima_calculator_ex.domain.DiceRollComposer
import com.javierdelgado.anima_calculator_ex.models.DiceRoll
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class DiceRollComposerTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun shouldComposeCorrectlyASimpleRoll() {
        val roll = DiceRoll(
            tag = "Attack roll",
            finalResult = 50,
            results = mutableListOf(50)
        )
        val composer = DiceRollComposer(context, roll)

        assertEquals(
            "Attack roll - Final result of 50.",
            composer.compose().toString()
        )
    }

    @Test
    fun shouldComposeCorrectlyAFumbleRoll() {
        val roll = DiceRoll(
            tag = "Attack roll",
            finalResult = 3,
            fumbleLevel = 40,
            results = mutableListOf(3)
        )
        val composer = DiceRollComposer(context, roll)

        assertEquals(
            "Attack roll - Final result of 3. You fumbled with a confirmation level of 40.",
            composer.compose().toString()
        )
    }

    @Test
    fun shouldComposeCorrectlyAnOpenRoll() {
        val roll = DiceRoll(
            tag = "Attack roll",
            finalResult = 220,
            results = mutableListOf(90, 100, 30),
            openRollCount = 2
        )
        val composer = DiceRollComposer(context, roll)

        assertEquals(
            "Attack roll - Final result of 220. 2 Open rolls. Your rolls: 90 -> 100 -> 30",
            composer.compose().toString()
        )
    }


    @Test
    fun shouldComposeCorrectlyAPalindromeRoll() {
        val roll = DiceRoll(
            tag = "Attack roll",
            finalResult = 225,
            results = mutableListOf(100, 95, 30),
            openRollCount = 2,
            confirmedPalindromeCount = 1
        )
        val composer = DiceRollComposer(context, roll)

        assertEquals(
            "Attack roll - Final result of 225. 2 Open rolls, 1 Palindrome. Your rolls: 100 -> 95 -> 30",
            composer.compose().toString()
        )
    }
}
