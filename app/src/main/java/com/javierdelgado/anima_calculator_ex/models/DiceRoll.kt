package com.javierdelgado.anima_calculator_ex.models

import com.javierdelgado.anima_calculator_ex.AppDatabase
import com.javierdelgado.anima_calculator_ex.utils.IntListConverter
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import java.util.*

@Table(database = AppDatabase::class)
data class DiceRoll(
    @PrimaryKey(autoincrement = true) var id: Int = 0,
    @Column var finalResult: Int = 0,
    @Column var openRollCount: Int = 0,
    @Column(typeConverter = IntListConverter::class) var results: MutableList<Int> = mutableListOf(),
    @Column var fumbleLevel: Int = 0,
    @Column var confirmedPalindromeCount: Int = 0,
    @Column var tag: String? = null,
    @Column var updatedAt: Date = Date()
) {
    fun didFumble(): Boolean {
        return fumbleLevel > 0
    }

    fun didOpenRoll(): Boolean {
        return openRollCount > 0
    }

    fun didPalindromeRoll(): Boolean {
        return confirmedPalindromeCount > 0
    }
}
