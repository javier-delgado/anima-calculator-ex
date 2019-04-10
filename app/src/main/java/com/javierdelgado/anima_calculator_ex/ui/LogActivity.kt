package com.javierdelgado.anima_calculator_ex.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javierdelgado.anima_calculator_ex.R
import com.javierdelgado.anima_calculator_ex.domain.DiceRollComposer
import com.javierdelgado.anima_calculator_ex.homeAsUp
import com.javierdelgado.anima_calculator_ex.inflate
import com.javierdelgado.anima_calculator_ex.models.DiceRoll
import com.javierdelgado.anima_calculator_ex.models.DiceRoll_Table.updatedAt
import com.raizlabs.android.dbflow.kotlinextensions.*
import kotlinx.android.synthetic.main.activity_log.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class LogActivity: AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LogActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        setSupportActionBar(toolbar)
        homeAsUp(true)

        doAsync {
            val rollList = ((select from DiceRoll::class).orderBy(updatedAt, false)).list
            uiThread {
                setupAdapter(rollList)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupAdapter(rollList: MutableList<DiceRoll>) {
        val adapter = LogAdapter(rollList)
        recLog.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recLog.adapter = adapter
    }
}

class LogAdapter(val rolls: List<DiceRoll>): RecyclerView.Adapter<LogEntryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogEntryViewHolder {
        val inflatedView = parent.inflate(R.layout.item_log_entry, false)
        return LogEntryViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return rolls.size
    }

    override fun onBindViewHolder(holder: LogEntryViewHolder, position: Int) {
        holder.bind(rolls[position])
    }

}

class LogEntryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(roll: DiceRoll) {
        itemView.findViewById<TextView>(R.id.txtLogEntry).text = DiceRollComposer(itemView.context, roll).compose()
    }
}