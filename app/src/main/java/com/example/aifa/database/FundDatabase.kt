package com.example.aifa.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Fund::class], version = 1, exportSchema = false)
@TypeConverters(FundTypeConverters::class)
abstract class FundDatabase : RoomDatabase() {
    abstract fun fundDao(): FundDao
}