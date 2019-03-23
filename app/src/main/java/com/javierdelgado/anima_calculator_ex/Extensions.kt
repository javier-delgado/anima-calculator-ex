package com.javierdelgado.anima_calculator_ex

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

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

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Context.createSimpleTextWatcher(afterTextChangedCallback: () -> Unit): TextWatcher {
    return object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChangedCallback()
        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    }
}

fun AppCompatActivity.homeAsUp(b: Boolean) {
    supportActionBar?.setDisplayHomeAsUpEnabled(b)
}
