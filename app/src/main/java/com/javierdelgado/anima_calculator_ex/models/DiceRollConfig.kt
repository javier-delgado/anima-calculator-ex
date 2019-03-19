package com.javierdelgado.anima_calculator_ex.models

data class DiceRollConfig(var openRollEnabled: Boolean = true,
                          var fumbleEnabled: Boolean = true,
                          var palindromeEnabled: Boolean = false,
                          var fumbleMaxValue: Int = 3,
                          var openRollMinValue: Int = 90)
