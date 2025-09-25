package com.example.financialapp.subscriptions

/*
this is the entity of a subscription
 */

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val amount: Int,
    val dueDate: String,
    val recurrence: String,
    val category: String,
    val reminder: Int = 3,
    val isActive: Boolean = true
)