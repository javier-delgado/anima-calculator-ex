package com.javierdelgado.anima_calculator_ex.models

import com.javierdelgado.anima_calculator_ex.AppDatabase
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.annotation.OneToMany
import com.raizlabs.android.dbflow.kotlinextensions.oneToMany
import com.raizlabs.android.dbflow.kotlinextensions.select
import io.paperdb.Paper


@Table(database = AppDatabase::class)
class Party(characters: MutableList<InitiativeCharacter> = mutableListOf()) {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column
    @Unique(onUniqueConflict = ConflictAction.REPLACE)
    var name: String = ""

    @get:OneToMany(methods = [OneToMany.Method.ALL])
    var characters by oneToMany {
        select.from(InitiativeCharacter::class.java)
            .where(InitiativeCharacter_Table.party_id.eq(this.id), InitiativeCharacter_Table.enemy.notEq(true))
    }

    companion object {
        private const val QUICK_SAVE_KEY = "quick_save_party"

        fun loadQuickSaveParty(): Party {
            return Paper.book().read(QUICK_SAVE_KEY, Party())
        }
    }

    init {
        this.characters = characters
    }

    fun quickSave() {
        Paper.book().write(QUICK_SAVE_KEY, this)
    }

    fun persisted(): Boolean {
        return id > 0
    }

}