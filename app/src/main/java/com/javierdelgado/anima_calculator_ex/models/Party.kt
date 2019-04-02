package com.javierdelgado.anima_calculator_ex.models

import com.javierdelgado.anima_calculator_ex.AppDatabase
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.annotation.OneToMany
import com.raizlabs.android.dbflow.kotlinextensions.oneToMany
import com.raizlabs.android.dbflow.kotlinextensions.*


@Table(database = AppDatabase::class)
class Party(name: String) {
    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column
    @Unique
    var name: String = ""

    @get:OneToMany(methods = [OneToMany.Method.ALL])
    var characters by oneToMany { select.from(InitiativeCharacter::class.java) where(InitiativeCharacter_Table.party_id.eq(id)) }

    companion object {
        const val DEFAULT_NAME = "___default_party___"
    }

    init {
        this.name = name
    }

    constructor(): this(DEFAULT_NAME)
}