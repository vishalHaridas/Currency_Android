package com.nagarro.currency.data.remote

import com.nagarro.currency.data.model.LatestRatesDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface PricesAPI {
    @GET("latest")
    suspend fun getLatestPrices(): LatestRatesDTO
}