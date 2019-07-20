package com.javierdelgado.anima_calculator_ex.ui.combat

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.javierdelgado.anima_calculator_ex.BuildConfig
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.createSimpleTextWatcher
import com.javierdelgado.anima_calculator_ex.domain.CombatResultComposer
import com.javierdelgado.anima_calculator_ex.domain.DiceRoller
import com.javierdelgado.anima_calculator_ex.models.Combat
import com.javierdelgado.anima_calculator_ex.showDiceRollSnackbar
import com.javierdelgado.anima_calculator_ex.ui.CriticalHitActivity
import com.javierdelgado.anima_calculator_ex.utils.MathEvaluator
import com.raizlabs.android.dbflow.kotlinextensions.save
import kotlinx.android.synthetic.main.fragment_combat_calculator.*
import org.jetbrains.anko.doAsync
import java.util.*

class CombatCalculatorFragment : Fragment(), Observer {
    private val combat: Combat = Combat()
    private val resultComposer by lazy { CombatResultComposer(context!!, combat) }
    private val modals by lazy { CombatCalculatorModals(context!!) }

    companion object {
        fun newInstance(): CombatCalculatorFragment {
            val fragment = CombatCalculatorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_combat_calculator, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.combat_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuCriticalHit -> CriticalHitActivity.start(context!!, combat.calculateDamageDealt())
            R.id.menuClear -> clearAll()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun clearAll() {
        combat.selectedDefenseModifiers = emptyList()
        combat.selectedAttackModifiers = emptyList()
        combat.ATValue = 0
        rdConsecutiveDefense1.isChecked = true
        edtAttackRoll.setText("")
        edtAttackFumbleLevel.setText("")
        edtFinalAttack.setText("")
        edtFinalDamage.setText("")
        edtDefenseRoll.setText("")
        edtDefenseFumbleLevel.setText("")
        edtFinalDefense.setText("")
        edtAT.setText("")
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
        btnEditAttackModifiers.text = resultComposer.totalAttackModifierText
        btnEditDefenseModifiers.text = resultComposer.totalDefenseModifierText
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
            onDiceRoll(getString(R.string.attack_roll), edtAttackRoll, edtAttackFumbleLevel, view)
        }

        btnRollDefenseDice.setOnClickListener { view ->
            onDiceRoll(getString(R.string.defense_roll), edtDefenseRoll, edtDefenseFumbleLevel, view)
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
            combat.attackRollValue = if (edtAttackRoll.text.isNotEmpty())
                MathEvaluator.evaluate(edtAttackRoll.text.toString())
            else
                0
        }
    }
    private val attackFumbleLevelTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.attackerFumbleLevel = if (edtAttackFumbleLevel.text.isNotEmpty())
                MathEvaluator.evaluate(edtAttackFumbleLevel.text.toString())
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
    private val defenseFumbleLevelTextWatcher by lazy {
        createSimpleTextWatcher {
            combat.defenderFumbleLevel = if (edtDefenseFumbleLevel.text.isNotEmpty())
                MathEvaluator.evaluate(edtDefenseFumbleLevel.text.toString())
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
            combat.characterDamage = if (edtFinalDamage.text.isNotEmpty())
                MathEvaluator.evaluate(edtFinalDamage.text.toString())
            else
                0
        }
    }

    private fun bindTextWatchers() {
        edtAttackRoll.addTextChangedListener(attackRollTextWatcher)
        edtAttackFumbleLevel.addTextChangedListener(attackFumbleLevelTextWatcher)
        edtFinalAttack.addTextChangedListener(finalAttackTextWatcher)
        edtDefenseRoll.addTextChangedListener(defenseRollTextWatcher)
        edtDefenseFumbleLevel.addTextChangedListener(defenseFumbleLevelTextWatcher)
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
        edtAttackFumbleLevel.removeTextChangedListener(attackFumbleLevelTextWatcher)
        edtFinalAttack.removeTextChangedListener(finalAttackTextWatcher)
        edtDefenseRoll.removeTextChangedListener(defenseRollTextWatcher)
        edtDefenseFumbleLevel.removeTextChangedListener(defenseFumbleLevelTextWatcher)
        edtFinalDefense.removeTextChangedListener(finalDefenseTextWatcher)
        edtFinalDamage.removeTextChangedListener(finalDamageTextWatcher)
    }

}