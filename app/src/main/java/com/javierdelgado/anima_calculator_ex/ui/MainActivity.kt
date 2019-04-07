package com.javierdelgado.anima_calculator_ex.ui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.javierdelgado.anima_calculator_ex.BuildConfig
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.domain.SettingsManager
import com.javierdelgado.anima_calculator_ex.models.DiceRollConfig
import com.javierdelgado.anima_calculator_ex.ui.combat.CombatCalculatorFragment
import com.javierdelgado.anima_calculator_ex.ui.initiative.InitiativeCalculatorFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = MainPagerAdapter(this, supportFragmentManager)
        txtVersion.text = getString(R.string.v_, BuildConfig.VERSION_NAME)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuSettings -> showSettings();
            R.id.menuLog -> LogActivity.start(this)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }


    fun showSettings(afterSave: () -> Unit = {}) {
        val dialog = MaterialDialog(this).show {
            title(R.string.settings)
            customView(R.layout.modal_settings)
            positiveButton(R.string.save) {
                val view = it.getCustomView()
                val allowOpenRoll = view.findViewById<CheckBox>(R.id.chkOpenRoll).isChecked
                val allowFumble = view.findViewById<CheckBox>(R.id.chkFumble).isChecked
                val allowPalindrome = view.findViewById<CheckBox>(R.id.chkPalindrome).isChecked
                val openRollMinValue = view.findViewById<EditText>(R.id.edtOpenRollMinValue).text
                val fumbleMaxValue = view.findViewById<EditText>(R.id.edtFumbleMaxvalue).text
                SettingsManager(context).save(
                    allowOpenRoll,
                    allowFumble,
                    allowPalindrome,
                    openRollMinValue.toString(),
                    fumbleMaxValue.toString()
                )
                afterSave()
            }
        }
        setupSettingsDialog(dialog)
    }

    private fun setupSettingsDialog(dialog: MaterialDialog) {
        DiceRollConfig.loadAsync {
            val view = dialog.getCustomView()
            view.findViewById<CheckBox>(R.id.chkOpenRoll).isChecked = it.openRollEnabled
            view.findViewById<CheckBox>(R.id.chkFumble).isChecked = it.fumbleEnabled
            view.findViewById<CheckBox>(R.id.chkPalindrome).isChecked = it.palindromeEnabled
            view.findViewById<EditText>(R.id.edtOpenRollMinValue).setText(it.openRollMinValue.toString())
            view.findViewById<EditText>(R.id.edtFumbleMaxvalue).setText(it.fumbleMaxValue.toString())
        }
    }
}

class MainPagerAdapter(val context: Context, fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    private val combatCalculatorFragment by lazy { CombatCalculatorFragment.newInstance() }
    private val initiativeCalculatorFragment by lazy { InitiativeCalculatorFragment.newInstance() }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> combatCalculatorFragment
            1 -> initiativeCalculatorFragment
            else -> throw RuntimeException("Fragment not found for MainPagerAdapter")
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> context.getString(R.string.combat)
            1 -> context.getString(R.string.initiative)
            else -> throw RuntimeException("Fragment not found for MainPagerAdapter")
        }
    }
}
