package com.example.financialapp.convertpage

data class ConvertResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)