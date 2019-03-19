package com.javierdelgado.anima_calculator_ex

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.javierdelgado.anima_calculator_ex.performers.DiceRoller
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        btnRollAttackDice.setOnClickListener { Log.d("debug", DiceRoller().perform().toString()) }
    }
}
