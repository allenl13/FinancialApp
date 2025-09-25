package com.example.financialapp.Conversion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConvertViewModel : ViewModel() {

    private val convertAPI = RetrofitInstance.convertAPI
    private val _convertResult = MutableStateFlow<ConvertResponse?>(null)
    val convertResult: StateFlow<ConvertResponse?> = _convertResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _showCurrency = MutableStateFlow<List<Currencies>>(emptyList())
    val showCurrencies: StateFlow<List<Currencies>> = _showCurrency

    private val _loadedCurrency = MutableStateFlow(false)
    val loadedCurrencies: StateFlow<Boolean> = _loadedCurrency

    init {
        loadAllCurrency()
    }

    // Loading all currency available from Frankfurter API
    private fun loadAllCurrency(){
        viewModelScope.launch {
            try {
                val apiConvert = convertAPI.allCurrency()

                val listOfCurrency = apiConvert
                    .toSortedMap()
                    .map { (code, name) ->
                        Currencies(
                            code = code,
                            name = name,
                            nameDisplay = "$code - $name",
                            mostUsed = false
                        )
                    }

                _showCurrency.value = listOfCurrency
                _loadedCurrency.value = true

            } catch (e: Exception){
                _loadedCurrency.value = true
                _error.value = "Failed loading currencies: ${e.message}"
            }
        }
    }

    fun getConverted(): List<Currencies>{
        return _showCurrency.value
    }

    // This function converts the currency to the specified currency
    fun convertMoney(amount : Double, from: String, to: String){
        viewModelScope.launch {
            try{
                _loading.value = true
                _error.value = null

                val response = convertAPI.getConversion(
                    amount = amount,
                    from = from,
                    to = to
                )
                _convertResult.value = response

            } catch(e: Exception){
                _error.value = "Convert Failed: ${e.message}"
                _convertResult.value = null
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError(){
        _error.value = null
    }
}