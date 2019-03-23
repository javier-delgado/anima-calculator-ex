package com.javierdelgado.anima_calculator_ex

import android.app.Application
import com.dbflow5.config.DatabaseConfig
import com.dbflow5.config.FlowConfig
import com.dbflow5.config.FlowManager
import com.dbflow5.database.AndroidSQLiteOpenHelper
import io.paperdb.Paper

class AnimaCalculatorEx: Application() {
    override fun onCreate() {
        super.onCreate()
        Paper.init(this)

        FlowManager.init(
            FlowConfig.Builder(this)
            .database(
                DatabaseConfig.builder(AppDatabase::class, AndroidSQLiteOpenHelper.createHelperCreator(this))
                .databaseName(AppDatabase.NAME)
                .build())
            .build())
    }

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }
}