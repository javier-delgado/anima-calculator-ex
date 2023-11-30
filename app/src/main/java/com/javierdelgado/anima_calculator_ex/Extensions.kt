package com.javierdelgado.anima_calculator_ex

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.javierdelgado.anima_calculator_ex.domain.DiceRollComposer
import com.javierdelgado.anima_calculator_ex.models.DiceRoll
import com.javierdelgado.anima_calculator_ex.ui.LogActivity

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

fun createSimpleTextWatcher(afterTextChangedCallback: () -> Unit): TextWatcher {
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

fun showDiceRollSnackbar(roll: DiceRoll, view: View) {
    Snackbar.make(view, DiceRollComposer(view.context, roll).compose(), Snackbar.LENGTH_LONG)
        .setAction(R.string.view_log) { LogActivity.start(view.context) }
        .show()
}

fun View.snackbar(@StringRes res: Int) {
    Snackbar.make(this, res, Snackbar.LENGTH_LONG).show();
}

fun showSoftKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}
