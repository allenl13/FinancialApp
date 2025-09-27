package com.example.financialapp.Investment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InvestViewModel(
    private val repo: InvestRepo
) : ViewModel() {

    // Live portfolio from the repository
    val portfolio: StateFlow<List<Investments>> =
        repo.observePortfolio()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Latest prices keyed by symbol (e.g., "AAPL" -> 233.12)
    private val _prices = MutableStateFlow<Map<String, Double>>(emptyMap())
    val prices: StateFlow<Map<String, Double>> = _prices

    fun addInvest(nameInvest: String, shares: Double, price: Double) {
        val item = Investments(
            nameInvest = nameInvest.trim().uppercase(),
            shares = shares,
            price = price,
            date = System.currentTimeMillis()
        )
        viewModelScope.launch { repo.add(item) }
    }

    fun totalInvested(): Double = cBasis()

    // Throttled in UI caller; fetch latest daily close and cache it
    suspend fun refreshQuote(nameInvest: String) {
        val result = RetrofitInstance.stockAPI.getDaily(symbol = nameInvest)
        if (result.isSuccessful) {
            val body = result.body() ?: return
            val ts = body.getAsJsonObject("Time Series (Daily)") ?: return
            val latestDate = ts.keySet().maxOrNull() ?: return
            val close = ts.getAsJsonObject(latestDate)["4. close"].asString.toDouble()
            _prices.update { it + (nameInvest to close) }
        }
    }

    // Cost basis = what you paid
    fun cBasis(): Double = portfolio.value.sumOf { it.shares * it.price }

    // Market value using latest fetched prices (fallback to buy price)
    fun marktValue(): Double =
        portfolio.value.sumOf { inv ->
            val px = prices.value[inv.nameInvest] ?: inv.price
            inv.shares * px
        }

    // Unrealised profit/loss
    fun unrealisedPL(): Double = marktValue() - cBasis()
}
