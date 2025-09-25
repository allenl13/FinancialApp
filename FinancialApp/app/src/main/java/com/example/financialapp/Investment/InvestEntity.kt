package com.example.financialapp.Investment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class InvestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val symbol: String, // nameInvest
    val shares: Double,
    val price: Double, // cost of invested
    val addedAt: Long // epoch millis
)
