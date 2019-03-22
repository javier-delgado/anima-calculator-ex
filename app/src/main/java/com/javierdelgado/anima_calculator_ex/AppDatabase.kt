package com.javierdelgado.anima_calculator_ex

import com.dbflow5.annotation.Database

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
class AppDatabase {
    companion object {
        const val NAME = "AppDatabase"
        const val VERSION = 1
    }
}