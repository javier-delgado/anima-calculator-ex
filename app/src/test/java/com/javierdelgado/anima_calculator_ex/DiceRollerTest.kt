package com.javierdelgado.anima_calculator_ex

import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import com.javierdelgado.anima_calculator_ex.models.DiceRollConfig
import io.mockk.*
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class DiceRollerTest {

    @Before
    fun mockPersistence() {
        mockkObject(DiceRollConfig)
        every { DiceRollConfig.loadSync() } returns DiceRollConfig(openRollEnabled = true, fumbleEnabled = true, palindromeEnabled = true)
    }

    @Test
    fun shouldCalculateSingleRollsCorrectly() {
        val roller = spyk<DiceRoller>(recordPrivateCalls = true)
        every { roller["random"]() } returns 10

        val roll = roller.perform()

        assertEquals(10, roll.finalResult)
        assertEquals(0, roll.openRollCount)
        assertEquals(mutableListOf(10), roll.results)
        assertEquals(0, roll.fumbleLevel)
        assertEquals(0, roll.confirmedPalindromeCount)
    }

    @Test
    fun shouldCalculateFumbleRollsCorrectly() {
        val roller = spyk<DiceRoller>(recordPrivateCalls = true)
        every { roller["random"]() }.returnsMany(2, 99)

        val roll = roller.perform()

        assertEquals(2, roll.finalResult)
        assertEquals(0, roll.openRollCount)
        assertEquals(mutableListOf(2), roll.results)
        assertEquals(99, roll.fumbleLevel)
        assertEquals(0, roll.confirmedPalindromeCount)
    }

    @Test
    fun shouldCalculateOpenRollsCorrectly() {
        val roller = spyk<DiceRoller>(recordPrivateCalls = true)
        every { roller["random"]() }.returnsMany(90, 91, 3)

        val roll = roller.perform()

        assertEquals(184, roll.finalResult)
        assertEquals(2, roll.openRollCount)
        assertEquals(mutableListOf(90, 91, 3), roll.results)
        assertEquals(0, roll.fumbleLevel)
        assertEquals(0, roll.confirmedPalindromeCount)
    }


    @Test
    fun shouldCalculatePalindromesCorrectly() {
        val roller = spyk<DiceRoller>(recordPrivateCalls = true)
        every { roller["random"]() }.returnsMany(55, 11, 92, 44, 30)

        val roll = roller.perform()

        assertEquals(236, roll.finalResult)
        assertEquals(2, roll.openRollCount)
        assertEquals(mutableListOf(100, 92, 44), roll.results)
        assertEquals(0, roll.fumbleLevel)
        assertEquals(1, roll.confirmedPalindromeCount)
    }
}
