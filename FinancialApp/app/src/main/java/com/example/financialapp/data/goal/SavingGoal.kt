package com.example.financialapp.data.goal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saving_goals")
data class SavingGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,    // epoch millis; null = no deadline
    val categoryId: Long? = null  // optional linkage to a saving category
)
