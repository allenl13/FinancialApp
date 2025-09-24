package com.example.financialapp.investmentpage

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val baseUrl = "https://www.alphavantage.co/"

    private fun getInstance() : Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val  stockAPI:  StockAPI= getInstance().create(StockAPI::class.java)
}