package com.example.financialapp.ui.transactions

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialapp.data.Transaction
import com.example.financialapp.util.TransactionExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionsViewModel : ViewModel() {


    var transactions: List<Transaction> = listOf(
        Transaction("2025-09-20", 7.50, "Cafe"),
        Transaction("2025-09-21", 12.34, "Groceries", "milk")
    )

    private val _exportResult = MutableStateFlow<Result<Uri>?>(null)
    val exportResult: StateFlow<Result<Uri>?> = _exportResult

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            try {
                val uri = TransactionExporter.exportToCsv(context, transactions)
                _exportResult.value = Result.success(uri)
            } catch (e: Exception) {
                _exportResult.value = Result.failure(e)
            }
        }
    }

    fun consumeExportResult() { _exportResult.value = null }
}
