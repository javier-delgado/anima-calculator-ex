package com.javierdelgado.anima_calculator_ex.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.javierdelgado.anima_calculator_ex.BuildConfig
import com.javierdelgado.anima_calculator_ex.R
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
