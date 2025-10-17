package com.example.financialapp.subscriptions

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
val isoFormat: DateTimeFormatter? = DateTimeFormatter.ISO_LOCAL_DATE

@RequiresApi(Build.VERSION_CODES.O)
val formatted: DateTimeFormatter? = DateTimeFormatter.ofPattern("dd MMM yyyy")

@RequiresApi(Build.VERSION_CODES.O)
fun parseAnyDate(value: String): LocalDate? {
    return try {
        LocalDate.parse(value, isoFormat)

    } catch (_: Exception) {
        try {
            LocalDate.parse(value, formatted)
        } catch (_: Exception) {
            null
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun nextBillingCycle(current: LocalDate, billingCycle: String): LocalDate {
    return when (billingCycle) {
        "day" -> current.plusDays(1)
        "week" -> current.plusWeeks(1)
        "month" -> current.plusMonths(1)
        "year" -> current.plusYears(1)
        else -> current.plusMonths(1)
    }
}