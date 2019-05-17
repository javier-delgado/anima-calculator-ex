package com.javierdelgado.anima_calculator_ex

import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter_Table
import com.raizlabs.android.dbflow.annotation.Database
import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration

@Database(version = AppDatabase.VERSION)
abstract class AppDatabase {
    companion object {
        const val NAME = "anima_calc_db"
        const val VERSION = 4
    }
}

@Migration(version = 4, database = AppDatabase::class)
class Migration2: AlterTableMigration<InitiativeCharacter>(InitiativeCharacter::class.java) {

    override fun onPreMigrate() {
        super.onPreMigrate()
        addColumn(SQLiteType.INTEGER, "uroboros")
    }
}