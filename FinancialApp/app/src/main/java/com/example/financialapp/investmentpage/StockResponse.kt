package com.example.financialapp.investmentpage

data class StockResponse(
    val nameStock: String,
    val price: Double,
    val change: Double,
    val chancePercent: Double
)
