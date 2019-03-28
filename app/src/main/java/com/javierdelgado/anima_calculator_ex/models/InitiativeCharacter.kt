package com.javierdelgado.anima_calculator_ex.models

import com.dbflow5.structure.save
import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import org.jetbrains.anko.doAsync
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
    var dataVisible: Boolean = false

    init {
        this.name = name
        this.base = base
    }

    fun totalInitiative(): Int = base + roll + fumble

    fun rollForInitiative(tag: String): DiceRoll {
        val config = DiceRollConfig.loadSync()
        config.fumbleEnabled = false

        val diceRoll = DiceRoller(config).perform()
        diceRoll.tag = tag
        roll = diceRoll.finalResult
        fumble = when(roll) {
            1 -> -125
            2 -> -100
            3 -> -75
            else -> 0
        }

        doAsync { diceRoll.save() }
        return diceRoll
    }


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