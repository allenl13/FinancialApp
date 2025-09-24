package com.example.financialapp.convertpage

import retrofit2.http.GET
import retrofit2.http.Query

interface ConvertAPI {
    @GET("v1/latest")
    suspend fun getConversion(
        @Query("amount") amount: Double,
        @Query("from") from: String,
        @Query("to") to: String
    ): ConvertResponse

    @GET("v1/currencies")
    suspend fun allCurrency(): Map<String, String>
}