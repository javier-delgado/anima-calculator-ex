package com.javierdelgado.anima_calculator_ex

import android.app.Application
import io.paperdb.Paper

class AnimaCalculatorEx: Application() {
    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
    }
}