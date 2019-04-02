package com.javierdelgado.anima_calculator_ex.models

import com.javierdelgado.anima_calculator_ex.AppDatabase
import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.save
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.properties.Delegates



@Table(database = AppDatabase::class, useBooleanGetterSetters = false)
class InitiativeCharacter(name: String, base: Int) : Observable() {
    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column
    var name: String = ""
        set(value) {
            field = value
            notifyObservers()
        }

    @Column
    var base: Int = 0
        set(value) {
            field = value
            notifyObservers()
        }


    @Column
    var roll: Int = 0
        set(value) {
            field = value
            notifyObservers()
        }


    @Column
    var fumble: Int = 0
        set(value) {
            field = value
            notifyObservers()
        }


    @Column
    var enemy: Boolean = false

    @ForeignKey(saveForeignKeyModel = false)
    var party: Party? = null

    var dataVisible: Boolean = false
    private val observers: MutableList<Observer> = mutableListOf<Observer>()

    init {
        this.name = name
        this.base = base
    }

    constructor() : this("", 0)

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