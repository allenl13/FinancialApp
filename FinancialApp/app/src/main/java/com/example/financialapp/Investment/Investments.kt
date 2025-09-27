package com.example.financialapp.Investment

data class Investments (
    val nameInvest: String, //Name of stock "ie GOOGL"
    val shares: Double,
    val price: Double,
    val date: Long //System.currentTimeMillis()
)