package com.example.financialapp.data



data class Transaction(
    val date: String,
    val amount: Double,
    val category: String,
    val note: String? = null
)
val list = listOf(
    Transaction("2025-09-21", 12.34, "Groceries", "milk"),
    Transaction("2025-09-20", 7.50, "Cafe", null)
)


val csv = buildString {
    appendLine("date,amount,category,note")
    for (t in list) {
        appendLine("${t.date},${t.amount},${t.category},${t.note ?: ""}")
    }
}