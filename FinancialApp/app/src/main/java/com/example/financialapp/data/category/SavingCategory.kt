package com.example.financialapp.data.category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saving_categories")
data class SavingCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String,     // "#FF6D00"
    val goalAmount: Double,   // editable per acceptance criteria
    val savedAmount: Double = 0.0
)
