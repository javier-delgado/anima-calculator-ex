package com.javierdelgado.anima_calculator_ex

import com.dbflow5.annotation.Database
import com.dbflow5.config.DBFlowDatabase

@Database(version = AppDatabase.VERSION)
abstract class AppDatabase: DBFlowDatabase() {
    companion object {
        const val NAME = "AppDatabase"
        const val VERSION = 1
    }
}
