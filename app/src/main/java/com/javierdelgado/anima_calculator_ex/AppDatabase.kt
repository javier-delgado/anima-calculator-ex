package com.javierdelgado.anima_calculator_ex

import com.raizlabs.android.dbflow.annotation.Database

@Database(version = AppDatabase.VERSION)
abstract class AppDatabase() {
    companion object {
        const val NAME = "anima_calc_db"
        const val VERSION = 1
    }
}
