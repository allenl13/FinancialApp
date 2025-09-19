package com.example.financialapp.Investment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class InvestViewModel : ViewModel(){
    private val _portfolio = MutableStateFlow<List<Investments>>(emptyList())
    val portfolio: StateFlow<List<Investments>> = _portfolio

    fun addInvest(
        nameInvest : String,
        shares: Double,
        price: Double
    ) {

        val addNewInvestment = Investments(
            nameInvest = nameInvest.trim().uppercase(),
            shares = shares,
            price = price,
            date = System.currentTimeMillis()
        )
        _portfolio.update {it + addNewInvestment}
    }

    fun totalInvested(
        currentPrice: Map<String, Double>
    ): Double{
        return _portfolio.value.sumOf { it.shares * it.price }
    }

}