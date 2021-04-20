package com.example.aifa.database

import androidx.room.*

@Entity
data class Fund(
    @PrimaryKey val id: String,
    val name: String,
    val wave: Double,
    val weight: Float,
    val pre_income:Float,
    val pre_loss:Float,
    var status:Int
)
