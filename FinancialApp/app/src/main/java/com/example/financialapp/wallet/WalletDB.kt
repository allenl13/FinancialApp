package com.example.financialapp.wallet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WalletEntity::class], version = 1)
abstract class WalletDB : RoomDatabase() {
    abstract fun walletCardDao(): WalletDao

    companion object {
        @Volatile private var INSTANCE: WalletDB? = null

        fun getDatabase(context: Context): WalletDB =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WalletDB::class.java,
                    "wallet.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}