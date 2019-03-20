package com.javierdelgado.anima_calculator_ex.models

import android.util.Log
import java.util.*
import kotlin.properties.Delegates

class Combat: Observable() {
    var selectedAttackModifiers: List<Modifier> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyObservers()
    }
    var selectedDefenseModifiers: List<Modifier>  by Delegates.observable(emptyList()) { _, _, _ ->
        notifyObservers()
    }
    var ATValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var consecutiveDefensePenalty: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var attackRollValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var defenseRollValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var characterAttackValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var characterDefenseValue: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var finalDamage: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }

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
