package com.javierdelgado.anima_calculator_ex.models

import com.dbflow5.annotation.Column
import com.dbflow5.annotation.PrimaryKey
import com.dbflow5.annotation.Table
import com.javierdelgado.anima_calculator_ex.AppDatabase

@Table(database = AppDatabase::class)
data class DiceRoll(
    @PrimaryKey var id: Int,
    @Column var finalResult: Int = 0,
    @Column var openRollCount: Int = 0,
    @Column val results: MutableList<Int> = mutableListOf(),
    @Column var fumbleLevel: Int = 0,
    @Column var confirmedPalindromeCount: Int = 0,
    @Column var tag: String? = null
)
