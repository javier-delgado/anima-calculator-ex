package com.javierdelgado.anima_calculator_ex.models

import com.javierdelgado.anima_calculator_ex.AppDatabase
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.annotation.OneToMany
import com.raizlabs.android.dbflow.kotlinextensions.oneToMany
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where


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
        const val DEFAULT_NAME = "___quick_save_party___"
        fun quickSaveParty(): Party {
            var aux = (select.from(Party::class.java) where (Party_Table.name.eq(DEFAULT_NAME))).querySingle()
            if(aux != null) return aux
            aux = Party(name = DEFAULT_NAME)
            aux.save()
            return aux
        }

    }

    init {
        this.name = name
    }

    constructor(): this(DEFAULT_NAME)
}