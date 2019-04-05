package com.javierdelgado.anima_calculator_ex

import android.app.Application
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter
import com.javierdelgado.anima_calculator_ex.models.InitiativeCharacter_Table
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.select
import io.paperdb.Paper

class AnimaCalculatorEx : Application() {
    override fun onCreate() {
        super.onCreate()
        Paper.init(this)

        FlowManager.init(
            FlowConfig.builder(this)
                .addDatabaseConfig(
                    DatabaseConfig.builder(AppDatabase::class.java)
                        .databaseName(AppDatabase.NAME)
                        .build()
                ).build()
        )
        cleanup()
    }

    private fun cleanup() {
        delete<InitiativeCharacter> {
            select.from(InitiativeCharacter::class.java)
                .where(InitiativeCharacter_Table.enemy.eq(true))
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }
}