package com.javierdelgado.anima_calculator_ex.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.createSimpleTextWatcher
import com.javierdelgado.anima_calculator_ex.databinding.ActivityCriticalHitBinding
import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import com.javierdelgado.anima_calculator_ex.homeAsUp
import com.javierdelgado.anima_calculator_ex.models.DiceRollConfig
import com.javierdelgado.anima_calculator_ex.showDiceRollSnackbar
import com.javierdelgado.anima_calculator_ex.utils.MathEvaluator
import com.raizlabs.android.dbflow.kotlinextensions.save
import org.jetbrains.anko.doAsync
import kotlin.math.ceil
import kotlin.properties.Delegates

class CriticalHitActivity : AppCompatActivity() {
    private var damageCaused: Int by Delegates.observable(0) { _, _, _ -> recalculateCritical() }
    private var criticalHitRoll: Int by Delegates.observable(0) { _, _, _ -> recalculateCritical() }
    private var physicalResistance: Int by Delegates.observable(0) { _, _, _ -> recalculateCritical() }
    private var rollResistance: Int by Delegates.observable(0) { _, _, _ -> recalculateCritical() }
    private var hasDamageResistance: Boolean by Delegates.observable(false) { _, _, _ -> recalculateCritical() }
    private val rollConfig: DiceRollConfig = DiceRollConfig(openRollEnabled = false, fumbleEnabled = false)
    private lateinit var binding: ActivityCriticalHitBinding

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
        binding = ActivityCriticalHitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        damageCaused = intent.getIntExtra(DAMAGE_CAUSED_EXTRA, 0)
        if (damageCaused > 0) binding.edtDamageCaused.setText(damageCaused.toString())
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
        val physicalRes = physicalResistance + rollResistance
        val critLevel = calculateCriticLevel(damageCaused + criticalHitRoll)

        binding.txtTotalResistance.text = getString(R.string.total_phys_res, physicalRes)
        binding.txtCritLevel.text = getString(R.string.crit_level, critLevel)

        if (rollResistance == 100) {
            binding.txtMainHeader.text = getString(R.string.defender_rolled_a_natural_100)
            binding.txtSecondaryHeader.text = getString(R.string.defender_endures_critical)
            return
        }


        if (physicalRes >= critLevel) {
            binding.txtMainHeader.text = ""
            binding.txtSecondaryHeader.text = getString(R.string.defender_endures_critical)
        } else {
            binding.txtMainHeader.text = getString(R.string.difference_, critLevel - physicalRes)
            binding.txtSecondaryHeader.text = getString(R.string.attacker_critical_hits)
        }
    }

    private fun calculateCriticLevel(tempCriticLevel: Int): Int {
        return when {
            hasDamageResistance -> ceil(tempCriticLevel/2.toDouble()).toInt()
            tempCriticLevel > 200 -> 200 + ceil((tempCriticLevel - 200) / 2.toDouble()).toInt()
            else -> tempCriticLevel
        }
    }

    private fun setupButtons() {
        binding.btnRollCritic.setOnClickListener {
            onDiceRoll(getString(R.string.critical_hit_roll), binding.edtCriticRoll, it)
        }
        binding.btnRollResistance.setOnClickListener {
            onDiceRoll(getString(R.string.phys_res_roll), binding.edtRollResistance, it)
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
        binding.edtDamageCaused.addTextChangedListener(damageCausedTextWatcher)
        binding.edtCriticRoll.addTextChangedListener(criticRollTextWatcher)
        binding.edtPhysicalResistance.addTextChangedListener(physicalResistanceTextWatcher)
        binding.edtRollResistance.addTextChangedListener(rollResistanceTextWatcher)
        binding.chkWithDamageResistance.setOnCheckedChangeListener { _, b -> hasDamageResistance = b }
    }

    private fun unbindTextWatchers() {
        binding.edtDamageCaused.removeTextChangedListener(damageCausedTextWatcher)
        binding.edtCriticRoll.removeTextChangedListener(criticRollTextWatcher)
        binding.edtPhysicalResistance.removeTextChangedListener(physicalResistanceTextWatcher)
        binding.edtRollResistance.removeTextChangedListener(rollResistanceTextWatcher)
        binding.chkWithDamageResistance.setOnCheckedChangeListener(null)
    }

    private val damageCausedTextWatcher by lazy {
        createSimpleTextWatcher {
            damageCaused = if (binding.edtDamageCaused.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtDamageCaused.text.toString())
            else
                0
        }
    }
    private val criticRollTextWatcher by lazy {
        createSimpleTextWatcher {
            criticalHitRoll = if (binding.edtCriticRoll.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtCriticRoll.text.toString())
            else
                0
        }
    }
    private val physicalResistanceTextWatcher by lazy {
        createSimpleTextWatcher {
            physicalResistance = if (binding.edtPhysicalResistance.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtPhysicalResistance.text.toString())
            else
                0
        }
    }
    private val rollResistanceTextWatcher by lazy {
        createSimpleTextWatcher {
            rollResistance = if (binding.edtRollResistance.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtRollResistance.text.toString())
            else
                0
        }
    }
}

