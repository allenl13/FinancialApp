package com.example.financialapp.Convertion

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ConvertAPI {
    @GET("v1/latest")
    suspend fun getConversion(
        @Query("amount") amount: Double,
        @Query("from") from: String,
        @Query("to") to: String
    ): Call<ConvertResponse>
}