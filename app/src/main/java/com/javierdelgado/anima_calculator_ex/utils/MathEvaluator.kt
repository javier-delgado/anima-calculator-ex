package com.javierdelgado.anima_calculator_ex.utils

import net.objecthunter.exp4j.ExpressionBuilder


class MathEvaluator {
    companion object {
        fun evaluate(text: String): Int {
            return try {
                ExpressionBuilder(text).build().evaluate().toInt()
            } catch(e: IllegalArgumentException) {
                0
            }
        }
    }
}