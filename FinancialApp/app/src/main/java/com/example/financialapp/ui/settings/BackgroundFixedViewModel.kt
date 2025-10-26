package com.example.financialapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialapp.data.prefs.BackgroundOptions
import com.example.financialapp.data.prefs.BackgroundPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BackgroundFixedViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = BackgroundPrefs(app)

    // Currently applied background (persisted)
    private val _selected = MutableStateFlow<Int?>(null)
    val selected: StateFlow<Int?> get() = _selected

    // Temporary choice on picker (not persisted yet)
    private val _tempChoice = MutableStateFlow<Int?>(null)
    val tempChoice: StateFlow<Int?> get() = _tempChoice

    // Six predefined options
    val options = BackgroundOptions.all

    init {
        viewModelScope.launch {
            val saved = prefs.selectedResIdFlow.first()
            _selected.value = saved
            _tempChoice.value = saved
        }
    }

    /** Choose a candidate (preview only) */
    fun chooseTemp(resId: Int) {
        _tempChoice.value = resId
    }

    /** Apply and persist */
    fun applyTemp() = viewModelScope.launch {
        val chosen = _tempChoice.value
        _selected.value = chosen

        if (chosen == null) {
            prefs.clear()
        } else {
            prefs.save(chosen)
        }
    }

    /** Reset to default (clear persisted value) */
    fun reset() = viewModelScope.launch {
        _selected.value = null
        _tempChoice.value = null
        prefs.clear()
    }
}