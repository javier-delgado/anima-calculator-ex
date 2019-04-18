package com.javierdelgado.anima_calculator_ex.models

import com.javierdelgado.anima_calculator_ex.AppDatabase
import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.kotlinextensions.save
import org.jetbrains.anko.doAsync
import java.util.*


@Table(database = AppDatabase::class, useBooleanGetterSetters = false)
class InitiativeCharacter(name: String, base: Int, enemy: Boolean) : Observable() {
    @PrimaryKey(autoincrement = true)
    @Transient
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

    @ForeignKey(stubbedRelationship = true)
    @Transient
    var party: Party? = null

    @Transient
    var dataVisible: Boolean = false

    @Transient
    private var observers: MutableList<Observer> = mutableListOf()

    init {
        this.name = name
        this.base = base
        this.enemy = enemy
    }

    constructor() : this("", 0, false)

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

    fun copy(): InitiativeCharacter {
        return InitiativeCharacter().apply {
            id = this@InitiativeCharacter.id
            name = this@InitiativeCharacter.name
            base = this@InitiativeCharacter.base
            roll = this@InitiativeCharacter.roll
            fumble = this@InitiativeCharacter.fumble
            enemy = this@InitiativeCharacter.enemy
            party = this@InitiativeCharacter.party
            dataVisible = this@InitiativeCharacter.dataVisible
            observers = this@InitiativeCharacter.observers
        }
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