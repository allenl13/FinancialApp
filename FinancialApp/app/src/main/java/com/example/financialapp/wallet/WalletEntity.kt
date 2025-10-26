package com.example.financialapp.wallet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_cards")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val network: String,
    val last4: String,
    val expire: String
)