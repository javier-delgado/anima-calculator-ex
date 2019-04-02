package com.javierdelgado.anima_calculator_ex

import android.app.Application
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
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

    }

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }
}