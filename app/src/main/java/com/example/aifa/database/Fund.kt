package com.example.aifa.database

import androidx.room.*

@Entity
data class Fund(
    @PrimaryKey val id: String,
    val name: String,
    val month1: Float,
    val month3: Float,
    val month6: Float,
    val today: Float,
    val highest: Float,
    val lowest: Float,
    val wave: Double,
    val past: Float,
    val present: Float,
    val weight: Float
)

//@Entity
data class Out(
    @PrimaryKey val fID:String,
    val part: Double,
    val factor: Double,
    val invest: Double,
    val everyday: Double
)