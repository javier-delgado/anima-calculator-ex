package com.javierdelgado.anima_calculator_ex.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.homeAsUp

class LogActivity: AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LogActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        homeAsUp(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}