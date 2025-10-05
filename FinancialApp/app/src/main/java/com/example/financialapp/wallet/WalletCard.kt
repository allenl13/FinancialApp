package com.example.financialapp.wallet

data class WalletCard(
    val id: Long,
    val name: String,
    val network: String,
    val last4: String,
    val expire: String
)
