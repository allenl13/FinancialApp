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

    val portfolio: StateFlow<List<Investments>> =
        repo.observePortfolio()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun totalInvested(): Double = portfolio.value.sumOf { it.shares * it.price }

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

    fun cBasis(): Double = portfolio.value.sumOf { it.shares * it.price }

    fun marktValue(): Double =
        portfolio.value.sumOf { inv ->
            val px = prices.value[inv.nameInvest] ?: inv.price
            inv.shares * px
        }

    fun unrealisedPL(): Double = marktValue() - cBasis()
}
//class InvestViewModel : ViewModel(){
//    private val _portfolio = MutableStateFlow<List<Investments>>(emptyList())
//    val portfolio: StateFlow<List<Investments>> = _portfolio
//    val _prices = MutableStateFlow<Map<String, Double>>(emptyMap())
//    val prices: StateFlow<Map<String, Double>> = _prices
//
//    fun addInvest(
//        nameInvest : String,
//        shares: Double,
//        price: Double
//    ) {
//
//        val addNewInvestment = Investments(
//            nameInvest = nameInvest.trim().uppercase(),
//            shares = shares,
//            price = price,
//            date = System.currentTimeMillis()
//        )
//        _portfolio.update {it + addNewInvestment}
//    }
//
//    fun totalInvested(
//        currentPrice: Map<String, Double>
//    ): Double{
//        return _portfolio.value.sumOf { it.shares * it.price }
//    }
//
//    //refreshing quote
//    suspend fun refreshQuote(nameInvest: String){
//        val result = RetrofitInstance.stockAPI.getDaily(symbol = nameInvest)
//        if(result.isSuccessful){
//            val body = result.body() ?: return
//
//            val ts = body.getAsJsonObject("Time Series (Daily)") ?: return
//            val latestDate = ts.keySet().maxOrNull() ?: return
//            val close = ts.getAsJsonObject(latestDate)["4. close"].asString.toDouble()
//            _prices.update { it + (nameInvest to close) }
//        }
//    }
//
//    // cost basis: showing what user paid
//    fun cBasis(): Double = _portfolio.value.sumOf { it.shares * it.price }
//
//    // market value with the latest prices
//    fun marktValue(): Double =
//        _portfolio.value.sumOf { inv ->
//            val px = _prices.value[inv.nameInvest] ?: inv.price
//            inv.shares * px
//        }
//
//    // P/L unreleased
//    fun unrealisedPL(): Double = marktValue() - cBasis()
//}