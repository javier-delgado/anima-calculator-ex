package com.javierdelgado.anima_calculator_ex.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.javierdelgado.anima_calculator_ex.R
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = MainPagerAdapter(supportFragmentManager)
    }
}

class MainPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    private val combatCalculatorFragment by lazy { CombatCalculatorFragment.newInstance() }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> combatCalculatorFragment
            1 -> CombatCalculatorFragment.newInstance() //TODO add other
            else -> throw RuntimeException("Fragment not found for MainPagerAdapter")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}