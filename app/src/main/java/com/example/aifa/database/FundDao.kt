package com.example.aifa.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FundDao {
    @Query("SELECT * FROM fund")
    fun getLiveFunds(): LiveData<List<Fund>>
    @Query("SELECT * FROM fund")
    fun getFunds(): List<Fund>

    @Query("SELECT * FROM fund WHERE id=(:id)")
    fun getLiveFund(id: String): LiveData<Fund>

    @Query("SELECT SUM(abs(wave)) FROM fund")
    fun getSumWave(): LiveData<Float>

    @Update
    fun updateFund(fund: Fund)

    @Insert
    fun addFund(fund: Fund)

    @Delete
    fun removeFund(fund: Fund)

    /*@Query("select * from out")
    fun getLiveOut(): LiveData<Out>

    @Insert(entity = Out::class)
    fun addOut(out: Out)

    @Update(entity = Out::class)
    fun updateOut(out: Out)
     */
}