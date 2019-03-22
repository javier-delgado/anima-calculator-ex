package com.javierdelgado.anima_calculator_ex.models

import android.util.Log
import java.util.*
import kotlin.properties.Delegates
import android.R



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
    var finalDamage: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }

    // Defense properties
    var selectedDefenseModifiers: List<Modifier>  by Delegates.observable(emptyList()) { _, _, _ ->
        notifyObservers()
    }
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

    fun totalAttack(): Int {
        return Math.max(selectedAttackModifiers.sumBy { it.value } + characterAttackValue + attackRollValue, 0);
    }

    fun totalDefense(): Int {
        val defenseSum = selectedDefenseModifiers.sumBy { it.value } + characterDefenseValue + defenseRollValue + consecutiveDefensePenalty
        return if (characterDefenseValue + attackRollValue>= 0)
            Math.max(defenseSum, 0);
        else
            defenseSum
    }

    fun result(): Int {
        return totalAttack() - totalDefense()
    }


    // Observer logic
    private val observers: MutableList<Observer> = mutableListOf<Observer>()

    override fun addObserver(o: Observer) {
        observers.add(o)
    }

    override fun deleteObserver(o: Observer?) {
        observers.remove(o)
    }

    override fun deleteObservers() {
        observers.clear()
    }

    override fun notifyObservers() {
        observers.forEach { observer -> observer.update(this, null) }
    }
}
