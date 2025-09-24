package com.example.financialapp.investmentpage

import com.example.financialapp.BuildConfig
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface StockAPI {
    @GET("query")
    suspend fun getDaily(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String, // Name of stock
        @Query("apikey") apiKey: String = BuildConfig.ALPHA_VANTAGE_KEY
    ): Response<JsonObject>
}