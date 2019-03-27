package com.javierdelgado.anima_calculator_ex.models

import java.util.*
import kotlin.properties.Delegates

class InitiativeCharacter(name: String, base: Int) : Observable() {
    private val observers: MutableList<Observer> = mutableListOf<Observer>()
    var name: String by Delegates.observable("") { _, _, _ ->
        notifyObservers()
    }
    var base: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var roll: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }
    var fumble: Int by Delegates.observable(0) { _, _, _ ->
        notifyObservers()
    }

    init {
        this.name = name
        this.base = base
    }

    fun totalInitiative(): Int = base + roll + fumble

    // Observer logic

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
        if (observers.isEmpty()) return
        observers.forEach { observer -> observer.update(this, null) }
    }
}