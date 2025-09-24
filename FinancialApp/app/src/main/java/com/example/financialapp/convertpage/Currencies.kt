package com.example.financialapp.convertpage

data class Currencies(
    val code: String,
    val name: String,
    val nameDisplay: String,
    val mostUsed: Boolean = false
)
