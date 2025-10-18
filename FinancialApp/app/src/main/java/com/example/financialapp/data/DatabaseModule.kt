package com.example.financialapp.data

import android.content.Context
import androidx.room.Room

object DatabaseModule {
    @Volatile private var instance: AppDatabase? = null

    fun db(context: Context): AppDatabase =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "financial-db"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
}