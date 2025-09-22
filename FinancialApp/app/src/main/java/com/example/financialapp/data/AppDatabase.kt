package com.example.financialapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financialapp.data.category.SavingCategory
import com.example.financialapp.data.category.SavingCategoryDao

@Database(
    entities = [SavingCategory::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savingCategoryDao(): SavingCategoryDao
}
