package com.example.aifa

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.aifa.database.Fund
import com.example.aifa.database.FundDatabase
import com.example.aifa.database.Out
import java.text.DecimalFormat
import java.util.concurrent.Executors
import kotlin.math.abs

private const val DATABASE_NAME = "fund-database"

class Repository private constructor(context: Context) {

    companion object {
        private var INSTANCE: Repository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = Repository(context)
            }
        }

        fun get(): Repository {
            return INSTANCE ?: throw IllegalStateException("Repository must be outialized")
        }
    }

    private val database: FundDatabase = Room.databaseBuilder(
        context.applicationContext,
        FundDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val fundDao = database.fundDao()
    private val executor = Executors.newSingleThreadExecutor()
    fun getLiveFunds() = fundDao.getLiveFunds()
    fun getLiveFund(id: String) = fundDao.getLiveFund(id)
    fun getFunds() = fundDao.getFunds()
    //fun getLiveOut() = fundDao.getLiveOut()
    fun getSumWave()=fundDao.getSumWave()

    fun updateFund(fund: Fund) {
        executor.execute {
            fundDao.updateFund(fund)
        }
    }

    fun removeFund(fund: Fund) {
        executor.execute {
            fundDao.removeFund(fund)
        }
    }

    fun addFund(fund: Fund) {
        executor.execute {
            fundDao.addFund(fund)
        }
    }

    /*fun addOut(out: Out) {
        executor.execute {
            fundDao.addOut(out)
        }
    }
    fun getAbs(): String {
        val list = getFunds()
        val dec= DecimalFormat("0.00")
        var a=0.0
        for (f in list) {
            a += abs(f.wave)
        }
        return dec.format(a).toString()
    }

    fun updateOut(out: Out) {
        executor.execute {
            fundDao.updateOut(out)
        }
    }*/

}