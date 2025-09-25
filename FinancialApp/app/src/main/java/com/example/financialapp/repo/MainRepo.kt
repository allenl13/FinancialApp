package com.example.financialapp.repo

class MainRepo{
    val items = mutableListOf(
        ExpenseDomain("Restaurant", 1000.00, "restaurant", "25 sept 2025 2:06"),
        ExpenseDomain("Investment", 2000.00, "market", "26 sept 2025 2:06"),
        ExpenseDomain("Connors", 3000.00, "trade", "27 sept 2025 2:06"),
        ExpenseDomain("PB Tech", 4000.00, "cinema", "28 sept 2025 2:06"),
        ExpenseDomain("KFC", 5000.00, "mcdonald", "29 sept 2025 2:06"),
    )
}
