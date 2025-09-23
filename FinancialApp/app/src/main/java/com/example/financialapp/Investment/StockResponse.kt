package com.example.financialapp.Investment

data class StockResponse(
    val nameStock: String,
    val price: Double,
    val change: Double,
    val chancePercent: Double
)
