package com.example.financialapp.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {

    private val _mode = MutableStateFlow(ThemeMode.SYSTEM)
    val mode: StateFlow<ThemeMode> = _mode


    private val _primaryArgb = MutableStateFlow(0xFF6750A4L)
    val primaryArgb: StateFlow<Long> = _primaryArgb

    fun setMode(newMode: ThemeMode) {
        _mode.value = newMode
    }

    fun setPrimaryColor(argb: Long) {
        _primaryArgb.value = argb
    }
}
