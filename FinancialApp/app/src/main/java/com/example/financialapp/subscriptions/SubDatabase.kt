package com.example.financialapp.subscriptions

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SubEntity::class], version = 1, exportSchema = false)
abstract class SubDatabase : RoomDatabase() {
    abstract fun subDao(): SubDao

    companion object {
        @Volatile
        private var INSTANCE: SubDatabase? = null

        fun getDatabase(context: Context): SubDatabase {
            val temp = INSTANCE
            if(temp != null) {
               return temp
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SubDatabase::class.java,
                    "sub_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}