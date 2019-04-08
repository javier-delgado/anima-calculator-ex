package com.javierdelgado.anima_calculator_ex.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.createSimpleTextWatcher
import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import com.javierdelgado.anima_calculator_ex.homeAsUp
import com.javierdelgado.anima_calculator_ex.models.DiceRollConfig
import com.javierdelgado.anima_calculator_ex.showDiceRollSnackbar
import com.javierdelgado.anima_calculator_ex.utils.MathEvaluator
import com.raizlabs.android.dbflow.kotlinextensions.save
import kotlinx.android.synthetic.main.activity_critical_hit.*
import org.jetbrains.anko.doAsync
import kotlin.properties.Delegates

class CriticalHitActivity: AppCompatActivity() {
    private var damageCaused: Int by Delegates.observable(0) { _, _, _ -> recalculateCritical() }
    private var criticalHitRoll: Int by Delegates.observable(0) { _, _, _ -> recalculateCritical() }
    private var physicalResistance: Int by Delegates.observable(0) { _, _, _ -> recalculateCritical() }
    private var rollResistance : Int by Delegates.observable(0) { _, _, _ -> recalculateCritical() }
    private val rollConfig: DiceRollConfig = DiceRollConfig(openRollEnabled = false, fumbleEnabled = false)

    companion object {
        private const val DAMAGE_CAUSED_EXTRA = "damage_caused"

        fun start(context: Context, initialDamage: Int) {
            context.startActivity(
                Intent(context, CriticalHitActivity::class.java).apply {
                    putExtra(DAMAGE_CAUSED_EXTRA, initialDamage)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_critical_hit)
        damageCaused = intent.getIntExtra(DAMAGE_CAUSED_EXTRA, 0)
        if(damageCaused > 0) edtDamageCaused.setText(damageCaused.toString())
        homeAsUp(true)
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        bindTextWatchers()
    }

    override fun onPause() {
        super.onPause()
        unbindTextWatchers()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun recalculateCritical() {
        if (rollResistance == 100) {
            txtMainHeader.text = getString(R.string.defender_rolled_a_natural_100)
            txtSecondaryHeader.text = getString(R.string.defender_endures_critical)
            return
        }

        val tempCriticLevel = damageCaused + criticalHitRoll
        val physicalRes = physicalResistance + rollResistance

        val critLevel = calculateCriticLevel(tempCriticLevel)

        if (physicalRes >= critLevel) {
            txtMainHeader.text = ""
            txtSecondaryHeader.text = getString(R.string.defender_endures_critical)
        } else {
            txtMainHeader.text = getString(R.string.crit_level, critLevel)
            txtSecondaryHeader.text = getString(R.string.attacker_critical_hits)
        }
    }

    private fun calculateCriticLevel(tempCriticLevel: Int): Int {
        return if ( tempCriticLevel > 200) {
            200 + Math.ceil((tempCriticLevel - 200) / 2.toDouble()).toInt()
        } else {
            tempCriticLevel
        }
    }

    private fun setupButtons() {
        btnRollCritic.setOnClickListener {
            onDiceRoll(getString(R.string.critical_hit_roll), edtCriticRoll, it)
        }
        btnRollResistance.setOnClickListener {
            onDiceRoll(getString(R.string.phys_res_roll), edtRollResistance, it)
        }
    }

    private fun onDiceRoll(tag: String, editText: EditText?, view: View) {
        val roll = DiceRoller(rollConfig).perform()
        roll.tag = tag
        editText?.setText(roll.finalResult.toString())
        showDiceRollSnackbar(roll, view)
        doAsync { roll.save() }
    }

    private fun bindTextWatchers() {
        edtDamageCaused.addTextChangedListener(damageCausedTextWatcher)
        edtCriticRoll.addTextChangedListener(criticRollTextWatcher)
        edtPhysicalResistance.addTextChangedListener(physicalResistanceTextWatcher)
        edtRollResistance.addTextChangedListener(rollResistanceTextWatcher)
    }

    private fun unbindTextWatchers() {
        edtDamageCaused.removeTextChangedListener(damageCausedTextWatcher)
        edtCriticRoll.removeTextChangedListener(criticRollTextWatcher)
        edtPhysicalResistance.removeTextChangedListener(physicalResistanceTextWatcher)
        edtRollResistance.removeTextChangedListener(rollResistanceTextWatcher)
    }

    private val damageCausedTextWatcher by lazy {
        createSimpleTextWatcher {
            damageCaused = if (edtDamageCaused.text.isNotEmpty())
                MathEvaluator.evaluate(edtDamageCaused.text.toString())
            else
                0
        }
    }
    private val criticRollTextWatcher by lazy {
        createSimpleTextWatcher {
            criticalHitRoll = if (edtCriticRoll.text.isNotEmpty())
                MathEvaluator.evaluate(edtCriticRoll.text.toString())
            else
                0
        }
    }
    private val physicalResistanceTextWatcher by lazy {
        createSimpleTextWatcher {
            physicalResistance = if (edtPhysicalResistance.text.isNotEmpty())
                MathEvaluator.evaluate(edtPhysicalResistance.text.toString())
            else
                0
        }
    }
    private val rollResistanceTextWatcher by lazy {
        createSimpleTextWatcher {
            rollResistance = if (edtRollResistance.text.isNotEmpty())
                MathEvaluator.evaluate(edtRollResistance.text.toString())
            else
                0
        }
    }
}

