package com.javierdelgado.anima_calculator_ex.models

import java.util.*
import kotlin.properties.Delegates
import kotlin.math.ceil
import kotlin.math.max

class Combat: Observable() {
    // Attack properties
    var selectedAttackModifiers: List<Modifier> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyObservers()
    }
    var attackRollValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var characterAttackValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var characterDamage: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }

    var attackerFumbleLevel: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }

    // Defense properties
    var selectedDefenseModifiers: List<Modifier>  by Delegates.observable(emptyList()) { _, _, _ ->
        notifyObservers()
    }
    // Note: the AT is used to calculate damage in CombatResultComposer, not actual defense value
    var ATValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var consecutiveDefensePenalty: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var defenseRollValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var characterDefenseValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }

    var defenderFumbleLevel: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }


    fun attackerFumbled(): Boolean {
        return attackerFumbleLevel > 0
    }

    fun defenderFumbled(): Boolean {
        return defenderFumbleLevel > 0
    }

    fun totalAttack(): Int {
        return max(selectedAttackModifiers.sumBy { it.value } + characterAttackValue + attackRollValue, 0);
    }

    fun totalDefense(): Int {
        var defenseSum = selectedDefenseModifiers.sumBy { it.value } + characterDefenseValue + defenseRollValue + consecutiveDefensePenalty
        if (defenderFumbled()) defenseSum -= defenderFumbleLevel
        return if (characterDefenseValue + attackRollValue>= 0)
            max(defenseSum, 0);
        else
            defenseSum
    }

    fun result(): Int {
        return totalAttack() - totalDefense()
    }

    fun calculateDamageDealt(): Int {
        return ceil(calculateDamagePercentage().toDouble() * characterDamage / 100).toInt()
    }

    fun calculateDamagePercentage(): Int {
        val absDifference: Int = result() - (20 + ATValue * 10)
        return absDifference / 10 * 10
    }

    // Observer logic
    private val observers: MutableList<Observer> = mutableListOf<Observer>()

    @Deprecated("Deprecated in Java")
    override fun addObserver(o: Observer) {
        observers.add(o)
    }

    @Deprecated("Deprecated in Java")
    override fun deleteObserver(o: Observer?) {
        observers.remove(o)
    }

    @Deprecated("Deprecated in Java")
    override fun deleteObservers() {
        observers.clear()
    }

    @Deprecated("Deprecated in Java")
    override fun notifyObservers() {
        observers.forEach { observer -> observer.update(this, null) }
    }
}
