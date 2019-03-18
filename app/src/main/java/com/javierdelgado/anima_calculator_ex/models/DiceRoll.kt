package com.javierdelgado.anima_calculator_ex.models

import android.R



data class DiceRoll(private val openRollEnabled: Boolean,
                    private val palindromeEnabled: Boolean,
                    private val fumbleEnabled: Boolean,
                    private val fumbleMaxValue: Int = 3,
                    private var openRollMinValue: Int = 90,
                    private var result: Int = 0) {


    fun roll() {
        val res = context.getResources()
        var diceResult: Int = (1..100).random()

        if (fumbleEnabled && diceResult <= fumbleMaxValue) {
            log = String.format(res.getString(R.string.tirada), diceResult) + "\n"
            log += res.getString(R.string.tirada_pifia) + "\n"
            val fumbleConfirmation = (1..100).random()

            if (diceResult == 1) log += String.format(res.getString(R.string.pifia_1), pifia += 15) + "\n"
            if (diceResult == 2) log += String.format(res.getString(R.string.pifia_2), pifia) + "\n"
            if (diceResult > 2) log += String.format(res.getString(R.string.pifia_3), pifia -= 15) + "\n"
            result = -fumbleConfirmation
            return
        }

        log = ""
        var total = 0
        var isOpenRoll: Boolean
        do {
            isOpenRoll = false
            log += String.format(res.getString(R.string.tirada_resultado), diceResult) + "\n"

            if (palindromeEnabled && diceResult / 10 == diceResult % 10) {
                val c = r.nextInt(10) + 1
                log += res.getString(R.string.tirada_capicua) + "\n"
                if (c == diceResult % 10) {
                    log += String.format(res.getString(R.string.tirada_capicua_si), diceResult % 10, c) + "\n"
                    diceResult = 100
                } else {
                    log += String.format(res.getString(R.string.tirada_capicua_no), diceResult % 10, c) + "\n"
                }
            }

            if (openRollEnabled && diceResult >= openRollMinValue) {
                isOpenRoll = true
                log += res.getString(R.string.tirada_abierta) + "\n"
                if (openRollMinValue< 100) openRollMinValue++
            }

            total += diceResult
            diceResult = (1..100).random()

        } while (isOpenRoll)

        log += String.format(res.getString(R.string.resultado), total)

        result = total
    }
}
