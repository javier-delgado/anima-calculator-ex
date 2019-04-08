package com.javierdelgado.anima_calculator_ex

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.javierdelgado.anima_calculator_ex.domain.CombatResultComposer
import com.javierdelgado.anima_calculator_ex.models.Combat
import io.mockk.every
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CombatResultComposerTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun composesTextCorrectlyWhenNothingHappens() {
        val combat = Combat()
        mockkObject(combat)
        every { combat.totalAttack() } returns 80
        every { combat.totalDefense() } returns 80

        val composer = CombatResultComposer(context, combat)
        composer.composeText()
        assertEquals("Nothing happens", composer.mainText)
        assertEquals("", composer.secondaryText)
        assertEquals("80", composer.totalAttackText)
        assertEquals("80", composer.totalDefenseText)
    }

    @Test
    fun composesTextCorrectlyWhenResultIsDefensive() {
        val combat = Combat()
        mockkObject(combat)
        every { combat.totalAttack() } returns 81
        every { combat.totalDefense() } returns 80

        val composer = CombatResultComposer(context, combat)
        composer.composeText()
        assertEquals("Defensive", composer.mainText)
        assertEquals("", composer.secondaryText)
        assertEquals("81", composer.totalAttackText)
        assertEquals("80", composer.totalDefenseText)
    }

    @Test
    fun composesTextCorrectlyWhenResultIsCounterAttackWithoutBonus() {
        val combat = Combat()
        mockkObject(combat)
        every { combat.totalAttack() } returns 80
        every { combat.totalDefense() } returns 81

        val composer = CombatResultComposer(context, combat)
        composer.composeText()
        assertEquals("Counter attack without bonus", composer.mainText)
        assertEquals("", composer.secondaryText)
        assertEquals("80", composer.totalAttackText)
        assertEquals("81", composer.totalDefenseText)
    }

    @Test
    fun composesTextCorrectlyWhenResultIsCounterAttackWithBonus() {
        val combat = Combat()
        mockkObject(combat)
        every { combat.totalAttack() } returns 80
        every { combat.totalDefense() } returns 90

        val composer = CombatResultComposer(context, combat)
        composer.composeText()
        assertEquals("Counter attack with +5 bonus", composer.mainText)
        assertEquals("", composer.secondaryText)
        assertEquals("80", composer.totalAttackText)
        assertEquals("90", composer.totalDefenseText)
    }

    @Test
    fun composesTextCorrectlyWhenDamageIsDealt() {
        val combat = Combat()
        mockkObject(combat)
        every { combat.totalAttack() } returns 130
        every { combat.totalDefense() } returns 90
        every { combat.characterDamage } returns 90

        val composer = CombatResultComposer(context, combat)
        composer.composeText()
        assertEquals("Damage dealt: 18", composer.mainText)
        assertEquals("20% out of 90 total damage", composer.secondaryText)
        assertEquals("130", composer.totalAttackText)
        assertEquals("90", composer.totalDefenseText)
    }
}