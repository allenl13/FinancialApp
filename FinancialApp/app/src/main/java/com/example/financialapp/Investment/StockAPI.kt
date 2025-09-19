package com.example.financialapp.Investment

import com.example.financialapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query


interface StockAPI {
    @GET("query")
    suspend fun getDaily(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String, // Name of stock
        @Query("apikey") apiKey: String = BuildConfig.ALPHA_VANTAGE_KEY
    ): StockResponse
}