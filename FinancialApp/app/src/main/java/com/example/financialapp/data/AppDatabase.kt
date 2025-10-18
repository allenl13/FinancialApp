package com.example.financialapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financialapp.data.category.SavingCategory
import com.example.financialapp.data.category.SavingCategoryDao
import com.example.financialapp.data.goal.SavingGoal
import com.example.financialapp.data.goal.SavingGoalDao

@Database(
    entities = [SavingCategory::class, SavingGoal::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savingCategoryDao(): SavingCategoryDao
    abstract fun savingGoalDao(): SavingGoalDao
}