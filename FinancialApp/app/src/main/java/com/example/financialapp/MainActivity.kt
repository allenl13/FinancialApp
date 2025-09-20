package com.example.financialapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.financialapp.ui.settings.SettingsScreen
import com.example.financialapp.ui.theme.AppThemeExt
import com.example.financialapp.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: ThemeViewModel = viewModel()
            val mode by vm.mode.collectAsState()
            val primary by vm.primaryArgb.collectAsState()

            AppThemeExt(mode = mode, primaryArgb = primary) {
                SettingsScreen(
                    currentMode = mode,
                    currentPrimary = primary,
                    onChangeMode = vm::setMode,
                    onChangeColor = vm::setPrimaryColor
                )
            }
        }
    }
}

