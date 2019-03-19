package com.javierdelgado.anima_calculator_ex

fun Int.isPalindrome(): Boolean {
    return this == this.reverse()
}

fun Int.reverse(): Int {
    var remainder: Int
    var reversed = 0
    var aux = this
    while (aux != 0) {
        remainder = aux % 10
        reversed = reversed * 10 + remainder
        aux /= 10
    }
    return reversed
}
