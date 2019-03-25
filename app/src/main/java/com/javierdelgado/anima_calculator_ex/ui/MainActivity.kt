package com.javierdelgado.anima_calculator_ex.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.dbflow5.structure.save
import com.google.android.material.snackbar.Snackbar
import com.javierdelgado.anima_calculator_ex.BuildConfig
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.createSimpleTextWatcher
import com.javierdelgado.anima_calculator_ex.domain.CombatResultComposer
import com.javierdelgado.anima_calculator_ex.domain.DiceRollComposer
import com.javierdelgado.anima_calculator_ex.models.Combat
import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import com.javierdelgado.anima_calculator_ex.models.DiceRoll
import com.javierdelgado.anima_calculator_ex.utils.MathEvaluator
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.util.*

class MainActivity : AppCompatActivity(), Observer {
    private val combat: Combat = Combat()
    private val resultComposer by lazy { CombatResultComposer(this, combat) }
    private val modals by lazy { MainActivityModals(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtVersion.text = getString(R.string.v_, BuildConfig.VERSION_NAME)
    }

    override fun onResume() {
        super.onResume()
        bindListeners()
        bindTextWatchers()
        combat.addObserver(this)
        showResult()
        combat.toString()
    }

    override fun onPause() {
        super.onPause()
        unbindListeners()
        unbindWatchers()
        combat.deleteObserver(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menuSettings -> modals.showSettings();
            R.id.menuLog -> LogActivity.start(this)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    // On combat changed
    override fun update(p0: Observable?, p1: Any?) {
        showResult()
    }

    private fun showResult() {
        resultComposer.composeText()
        txtMainHeader.text = resultComposer.mainText
        txtSecondaryHeader.text = resultComposer.secondaryText
        txtTotalAttack.text = resultComposer.totalAttackText
        txtTotalDefense.text = resultComposer.totalDefenseText
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

        btnRollAttackDice.setOnClickListener { view ->
            onDiceRoll(getString(R.string.attack_roll), edtAttackRoll, view)
        }

        btnRollDefenseDice.setOnClickListener {view ->
            onDiceRoll(getString(R.string.defense_roll), edtDefenseRoll, view)
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

    private fun onDiceRoll(tag: String, editText: EditText?, view: View) {
        val roll = DiceRoller().perform()
        roll.tag = tag
        editText?.setText(roll.finalResult.toString())
        showDiceRollSnackbar(roll, view)
        doAsync { roll.save() }
    }

    private fun showDiceRollSnackbar(roll: DiceRoll, view: View) {
        Snackbar.make(view, DiceRollComposer(this, roll).compose(), Snackbar.LENGTH_LONG)
            .setAction(R.string.view_log) { LogActivity.start(this) }
            .show();
    }

    private val attackRollTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.attackRollValue = if (edtAttackRoll.text.isNotEmpty())
                MathEvaluator.evaluate(edtAttackRoll.text.toString())
            else
                0
        }
    }
    private val finalAttackTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.characterAttackValue = if (edtFinalAttack.text.isNotEmpty())
                MathEvaluator.evaluate(edtFinalAttack.text.toString())
            else
                0
        }
    }
    private val defenseRollTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.defenseRollValue = if (edtDefenseRoll.text.isNotEmpty())
                MathEvaluator.evaluate(edtDefenseRoll.text.toString())
            else
                0
        }
    }
    private val finalDefenseTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.characterDefenseValue = if (edtFinalDefense.text.isNotEmpty())
                MathEvaluator.evaluate(edtFinalDefense.text.toString())
            else
                0
        }
    }
    private val finalDamageTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.finalDamage = if (edtFinalDamage.text.isNotEmpty())
                MathEvaluator.evaluate(edtFinalDamage.text.toString())
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
