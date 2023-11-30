package com.javierdelgado.anima_calculator_ex.ui.combat

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.javierdelgado.anima_calculator_ex.BuildConfig
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.createSimpleTextWatcher
import com.javierdelgado.anima_calculator_ex.databinding.FragmentCombatCalculatorBinding
import com.javierdelgado.anima_calculator_ex.domain.CombatResultComposer
import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import com.javierdelgado.anima_calculator_ex.models.Combat
import com.javierdelgado.anima_calculator_ex.showDiceRollSnackbar
import com.javierdelgado.anima_calculator_ex.ui.CriticalHitActivity
import com.javierdelgado.anima_calculator_ex.utils.MathEvaluator
import com.raizlabs.android.dbflow.kotlinextensions.save
import org.jetbrains.anko.doAsync
import java.util.*

class CombatCalculatorFragment : Fragment(), Observer {
    private val combat: Combat = Combat()
    private val resultComposer by lazy { CombatResultComposer(requireContext(), combat) }
    private val modals by lazy { CombatCalculatorModals(requireContext()) }
    private var _binding: FragmentCombatCalculatorBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): CombatCalculatorFragment {
            val fragment = CombatCalculatorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCombatCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtVersion.text = getString(R.string.v_, BuildConfig.VERSION_NAME)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.combat_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menuCriticalHit -> CriticalHitActivity.start(requireContext(), combat.calculateDamageDealt())
            R.id.menuClear -> clearAll()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun clearAll() {
        combat.selectedDefenseModifiers = emptyList()
        combat.selectedAttackModifiers = emptyList()
        combat.ATValue = 0
        binding.rdConsecutiveDefense1.isChecked = true
        binding.edtAttackRoll.setText("")
        binding.edtAttackFumbleLevel.setText("")
        binding.edtFinalAttack.setText("")
        binding.edtFinalDamage.setText("")
        binding.edtDefenseRoll.setText("")
        binding.edtDefenseFumbleLevel.setText("")
        binding.edtFinalDefense.setText("")
        binding.edtAT.setText("")
    }

    // On combat changed
    @Deprecated("Deprecated in Java")
    override fun update(p0: Observable?, p1: Any?) {
        showResult()
    }

    private fun showResult() {
        resultComposer.composeText()
        binding.txtMainHeader.text = resultComposer.mainText
        binding.txtSecondaryHeader.text = resultComposer.secondaryText
        binding.txtTotalAttack.text = resultComposer.totalAttackText
        binding.txtTotalDefense.text = resultComposer.totalDefenseText
        binding.btnEditAttackModifiers.text = resultComposer.totalAttackModifierText
        binding.btnEditDefenseModifiers.text = resultComposer.totalDefenseModifierText
    }

    private fun bindListeners() {
        binding.btnEditAttackModifiers.setOnClickListener {
            modals.showAttackModifiers(combat.selectedAttackModifiers) { newAttackModifiers ->
                combat.selectedAttackModifiers = newAttackModifiers
            }
        }

        binding.btnEditDefenseModifiers.setOnClickListener {
            modals.showDefenseModifiers(combat.selectedDefenseModifiers) { newDefenseModifiers ->
                combat.selectedDefenseModifiers = newDefenseModifiers
            }
        }

        binding.btnRollAttackDice.setOnClickListener { view ->
            onDiceRoll(getString(R.string.attack_roll), binding.edtAttackRoll, binding.edtAttackFumbleLevel, view)
        }

        binding.btnRollDefenseDice.setOnClickListener { view ->
            onDiceRoll(getString(R.string.defense_roll), binding.edtDefenseRoll, binding.edtDefenseFumbleLevel, view)
        }

        binding.rdGroupConsecutiveDefense.setOnCheckedChangeListener { _, rdId ->
            combat.consecutiveDefensePenalty = when (rdId) {
                R.id.rdConsecutiveDefense1 -> 0
                R.id.rdConsecutiveDefense2 -> -30
                R.id.rdConsecutiveDefense3 -> -50
                R.id.rdConsecutiveDefense4 -> -70
                R.id.rdConsecutiveDefense5 -> -90
                else -> 0
            }
        }

        binding.edtAT.setOnClickListener {
            modals.showATSelector { ATValue ->
                combat.ATValue = ATValue
                binding.edtAT.setText(getString(R.string.at_, ATValue))
            }
        }
    }

    private fun onDiceRoll(tag: String, editText: EditText, fumbleEditText: EditText, view: View) {
        val roll = DiceRoller().perform()
        roll.tag = tag
        editText.setText(roll.finalResult.toString())
        fumbleEditText.setText(if(roll.fumbleLevel != 0) roll.fumbleLevel.toString() else "")
        showDiceRollSnackbar(roll, view)
        doAsync { roll.save() }
    }

    private val attackRollTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.attackRollValue = if (binding.edtAttackRoll.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtAttackRoll.text.toString())
            else
                0
        }
    }
    private val attackFumbleLevelTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.attackerFumbleLevel = if (binding.edtAttackFumbleLevel.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtAttackFumbleLevel.text.toString())
            else
                0
        }
    }
    private val finalAttackTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.characterAttackValue = if (binding.edtFinalAttack.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtFinalAttack.text.toString())
            else
                0
        }
    }
    private val defenseRollTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.defenseRollValue = if (binding.edtDefenseRoll.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtDefenseRoll.text.toString())
            else
                0
        }
    }
    private val defenseFumbleLevelTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.defenderFumbleLevel = if (binding.edtDefenseFumbleLevel.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtDefenseFumbleLevel.text.toString())
            else
                0
        }
    }
    private val finalDefenseTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.characterDefenseValue = if (binding.edtFinalDefense.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtFinalDefense.text.toString())
            else
                0
        }
    }
    private val finalDamageTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.characterDamage = if (binding.edtFinalDamage.text.isNotEmpty())
                MathEvaluator.evaluate(binding.edtFinalDamage.text.toString())
            else
                0
        }
    }

    private fun bindTextWatchers() {
        binding.edtAttackRoll.addTextChangedListener(attackRollTextWatcher)
        binding.edtAttackFumbleLevel.addTextChangedListener(attackFumbleLevelTextWatcher)
        binding.edtFinalAttack.addTextChangedListener(finalAttackTextWatcher)
        binding.edtDefenseRoll.addTextChangedListener(defenseRollTextWatcher)
        binding.edtDefenseFumbleLevel.addTextChangedListener(defenseFumbleLevelTextWatcher)
        binding.edtFinalDefense.addTextChangedListener(finalDefenseTextWatcher)
        binding.edtFinalDamage.addTextChangedListener(finalDamageTextWatcher)
    }

    private fun unbindListeners() {
        binding.btnEditAttackModifiers.setOnClickListener(null)
        binding.btnEditDefenseModifiers.setOnClickListener(null)
        binding.btnRollAttackDice.setOnClickListener(null)
        binding.btnRollDefenseDice.setOnClickListener(null)
        binding.rdGroupConsecutiveDefense.setOnClickListener(null)
        binding.edtAT.setOnClickListener(null)
    }

    private fun unbindWatchers() {
        binding.edtAttackRoll.removeTextChangedListener(attackRollTextWatcher)
        binding.edtAttackFumbleLevel.removeTextChangedListener(attackFumbleLevelTextWatcher)
        binding.edtFinalAttack.removeTextChangedListener(finalAttackTextWatcher)
        binding.edtDefenseRoll.removeTextChangedListener(defenseRollTextWatcher)
        binding.edtDefenseFumbleLevel.removeTextChangedListener(defenseFumbleLevelTextWatcher)
        binding.edtFinalDefense.removeTextChangedListener(finalDefenseTextWatcher)
        binding.edtFinalDamage.removeTextChangedListener(finalDamageTextWatcher)
    }

}