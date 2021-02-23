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
            val goal = mutableListOf<String>()
            run loop@{
                funds.forEach {
                    if (it.status == 1) {
                        val newIt = loadFund(applicationContext, it.id)
                        if (newIt != null) {
                            repository.updateFund(newIt)
                            if (newIt.wave < -5 && newIt.weight > 0.5) {
                                goal.add(newIt.name)
                            }
                        } else {
                            return@loop
                        }
                    }
                    //Thread.sleep(time*1000)
                }
            }
            /*for (f in funds) {
                if (f.status == 1) {
                    val newIt = loadFund(applicationContext, f.id)
                    if (newIt != null) {
                        repository.updateFund(newIt)
                        if (newIt.wave < -5 && newIt.weight > 0.5) {
                            goal.add(newIt.name)
                        }
                    } else {
                        break
                    }
                }
            }
             */
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