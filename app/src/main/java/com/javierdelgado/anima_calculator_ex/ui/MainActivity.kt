package com.javierdelgado.anima_calculator_ex.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.createSimpleTextWatcher
import com.javierdelgado.anima_calculator_ex.models.Combat
import com.javierdelgado.anima_calculator_ex.performers.DiceRoller
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), Observer {
    private val combat = Combat()

    private val modals by lazy { MainActivityModals(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        bindListeners()
        bindTextWatchers()
        combat.addObserver(this)
    }

    override fun onPause() {
        super.onPause()
        unbindListeners()
        unbindWatchers()
        combat.deleteObserver(this)
    }

    // On combat changed
    override fun update(p0: Observable?, p1: Any?) {
        calculateResult()
    }

    private fun calculateResult() {
        Log.d("debug", "calculate result")
    }

    private fun bindListeners() {
        btnEditAttackModifiers.setOnClickListener {
            modals.showAttackModifiers(combat.selectedAttackModifiers) { newAttackModifiers ->
                combat.selectedAttackModifiers = newAttackModifiers
            }
        }

        btnEditDefenseModifiers.setOnClickListener {
            modals.showDefenseModifiers(combat.selectedDefenseModifiers) { newDefenseModifiers ->
                combat.selectedDefenseModifiers = newDefenseModifiers
            }
        }

        btnRollAttackDice.setOnClickListener {
            val roll = DiceRoller().perform()
            edtAttackRoll.setText(roll.finalResult.toString())
        }

        btnRollDefenseDice.setOnClickListener {
            val roll = DiceRoller().perform()
            edtDefenseRoll.setText(roll.finalResult.toString())
        }

        rdGroupConsecutiveDefense.setOnCheckedChangeListener { _, rdId ->
            combat.consecutiveDefensePenalty = when (rdId) {
                R.id.rdConsecutiveDefense1 -> 0
                R.id.rdConsecutiveDefense2 -> -30
                R.id.rdConsecutiveDefense3 -> -50
                R.id.rdConsecutiveDefense4 -> -70
                R.id.rdConsecutiveDefense5 -> -90
                else -> 0
            }
        }

        edtAT.setOnClickListener {
            modals.showATSelector { ATValue ->
                combat.ATValue = ATValue
                edtAT.setText(getString(R.string.at_, ATValue))
            }
        }
    }

    private val attackRollTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.attackRollValue = if (edtAttackRoll.text.isNotEmpty())
                edtAttackRoll.text.toString().toInt()
            else
                0
        }
    }
    private val finalAttackTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.characterAttackValue = if (edtFinalAttack.text.isNotEmpty())
                edtFinalAttack.text.toString().toInt()
            else
                0
        }
    }
    private val defenseRollTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.defenseRollValue = if (edtDefenseRoll.text.isNotEmpty())
                edtDefenseRoll.text.toString().toInt()
            else
                0
        }
    }
    private val finalDefenseTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.characterDefenseValue = if (edtFinalDefense.text.isNotEmpty())
                edtFinalDefense.text.toString().toInt()
            else
                0
        }
    }
    private val finalDamageTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.finalDamage = if (edtFinalDamage.text.isNotEmpty())
                edtFinalDamage.text.toString().toInt()
            else
                0
        }
    }

    private fun bindTextWatchers() {
        edtAttackRoll.addTextChangedListener(attackRollTextWatcher)
        edtFinalAttack.addTextChangedListener(finalAttackTextWatcher)
        edtDefenseRoll.addTextChangedListener(defenseRollTextWatcher)
        edtFinalDefense.addTextChangedListener(finalDefenseTextWatcher)
        edtFinalDamage.addTextChangedListener(finalDamageTextWatcher)
    }

    private fun unbindListeners() {
        btnEditAttackModifiers.setOnClickListener(null)
        btnEditDefenseModifiers.setOnClickListener(null)
        btnRollAttackDice.setOnClickListener(null)
        btnRollDefenseDice.setOnClickListener(null)
        rdGroupConsecutiveDefense.setOnClickListener(null)
        edtAT.setOnClickListener(null)
    }

    private fun unbindWatchers() {
        edtAttackRoll.removeTextChangedListener(attackRollTextWatcher)
        edtFinalAttack.removeTextChangedListener(finalAttackTextWatcher)
        edtDefenseRoll.removeTextChangedListener(defenseRollTextWatcher)
        edtFinalDefense.removeTextChangedListener(finalDefenseTextWatcher)
        edtFinalDamage.removeTextChangedListener(finalDamageTextWatcher)
    }

}
