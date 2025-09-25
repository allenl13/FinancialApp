package com.example.financialapp.Investment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InvestVMFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db   = InvestDBModule.db(context)
        val repo = InvestRepo(db.investDao())
        return InvestViewModel(repo) as T
    }
}