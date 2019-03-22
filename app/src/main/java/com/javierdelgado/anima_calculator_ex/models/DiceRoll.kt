package com.javierdelgado.anima_calculator_ex.models

data class DiceRoll(
    var finalResult: Int = 0,
    var openRollCount: Int = 0,
    val results: MutableList<Int> = mutableListOf<Int>(),
    var fumbleLevel: Int = 0,
    var confirmedPalindromeCount: Int = 0
)
