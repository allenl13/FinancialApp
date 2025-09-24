package com.example.financialapp.investmentpage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [InvestEntity::class],
    version = 1,
    exportSchema = true
)
abstract class InvestDB: RoomDatabase() {
    abstract fun investDao(): InvestDao
}

object InvestDBModule{
    @Volatile private var instance: InvestDB? = null
    fun db(context: Context): InvestDB =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                InvestDB::class.java,
                "portfolio.db"
            ).fallbackToDestructiveMigration().build().also { instance = it }
        }
}
