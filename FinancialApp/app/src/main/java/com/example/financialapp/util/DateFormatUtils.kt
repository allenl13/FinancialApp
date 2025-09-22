package com.example.financialapp.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

val DMY: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd/MM/uuuu")
    .withResolverStyle(ResolverStyle.STRICT)

fun Long?.formatAsDmyOrNull(): String? = this?.let {
    Instant.ofEpochMilli(it)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DMY)
}
