package com.example.financialapp.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppThemeExt(mode: `ThemeMode`, primaryArgb: Long, content: @Composable () -> Unit) {
    val isDark = when (mode) {
        `ThemeMode`.SYSTEM -> isSystemInDarkTheme()
        `ThemeMode`.LIGHT -> false
        `ThemeMode`.DARK -> true
    }
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = base.copy(primary = Color(primaryArgb))) { content() }
}
