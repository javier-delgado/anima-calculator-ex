package com.javierdelgado.anima_calculator_ex

import com.javierdelgado.anima_calculator_ex.models.Combat
import com.javierdelgado.anima_calculator_ex.models.Modifier
import junit.framework.TestCase.assertEquals
import org.junit.Test

class CombatTest {
    @Test
    fun calculatesCorrectly() {
        val combat = Combat()
        combat.attackRollValue = 60
        combat.characterAttackValue = 150
        combat.selectedAttackModifiers = mutableListOf(Modifier("TestMod1", 20), Modifier("TestMod2", 10))
        combat.finalDamage = 90
        combat.defenseRollValue = 80
        combat.characterDefenseValue = 120
        combat.consecutiveDefensePenalty = -30
        combat.selectedDefenseModifiers = mutableListOf(Modifier("TestMod3", -90))
        combat.ATValue = 20

        assertEquals(80 ,combat.totalDefense())
        assertEquals(240 ,combat.totalAttack())
        assertEquals(160 ,combat.result())
    }
}