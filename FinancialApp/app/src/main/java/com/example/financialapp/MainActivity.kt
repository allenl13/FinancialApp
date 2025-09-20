package com.example.financialapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialapp.data.Transaction
import com.example.financialapp.ui.settings.SettingsScreen
import com.example.financialapp.ui.theme.AppThemeExt
import com.example.financialapp.ui.theme.ThemeViewModel
import com.example.financialapp.ui.transactions.TransactionsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeVm: ThemeViewModel = viewModel()
            val txVm: TransactionsViewModel = viewModel()

            val mode by themeVm.mode.collectAsState()
            val primary by themeVm.primaryArgb.collectAsState()
            val exportResult by txVm.exportResult.collectAsState()

            AppThemeExt(mode = mode, primaryArgb = primary) {
                SettingsScreen(
                    currentMode = mode,
                    currentPrimary = primary,
                    onChangeMode = themeVm::setMode,
                    onChangeColor = themeVm::setPrimaryColor,
                    onExportCsv = { txVm.exportCsv(this@MainActivity) }
                )
            }


            LaunchedEffect(exportResult) {
                exportResult?.let { res ->
                    if (res.isSuccess) {
                        Toast.makeText(
                            this@MainActivity,
                            "CSV saved: ${res.getOrNull()}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Export failed: ${res.exceptionOrNull()?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    txVm.consumeExportResult()
                }
            }
        }


        val list: List<Transaction> = listOf(
            Transaction("2025-09-21", 12.34, "Groceries", "milk"),
            Transaction("2025-09-20", 7.50, "Cafe", null)
        )
        val csv = buildString {
            appendLine("date,amount,category,note")
            for (t in list) appendLine("${t.date},${t.amount},${t.category},${t.note ?: ""}")
        }
        Log.d("CSV_TEST", csv)
    }
}
