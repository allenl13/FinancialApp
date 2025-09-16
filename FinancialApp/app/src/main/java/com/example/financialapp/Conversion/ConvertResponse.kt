package com.example.financialapp.Convertion

data class ConvertResponse(
    val amount: Double, //current amount
    val base: String, //current currency
    val date: String,
    val rates: Map<String, Double>
)