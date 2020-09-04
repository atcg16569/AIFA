package com.example.aifa

import android.content.Context
import androidx.room.Room
import com.example.aifa.database.Fund
import com.example.aifa.database.FundDatabase
import java.util.concurrent.Executors

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

}