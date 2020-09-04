package com.example.aifa

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {
        val cal = Calendar.getInstance()
        if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            val repository = Repository.get()
            val funds = repository.getFunds()
            funds.forEach {
                val newIt = fund(it.id)
                repository.updateFund(newIt)
                //Thread.sleep(time*1000)
            }
            val newFunds = repository.getFunds()
            val goal = mutableListOf<String>()
            for (i in newFunds) {
                if (i.wave < -5 && i.weight > 0.5) {
                    goal.add(i.name)
                }
            }
            if (goal.isNotEmpty()) {
                notice("Funds less than -5%", goal.joinToString(), applicationContext)
            }
        }
        // timePre
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = pref.edit()
        if (pref.getString("update", "UpdateTime") != "UpdateTime") {
            editor.remove("update")
        }
        editor.putString("update", cal.time.toString()).apply()
        return Result.success()
    }
}