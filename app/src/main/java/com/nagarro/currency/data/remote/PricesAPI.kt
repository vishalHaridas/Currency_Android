package com.nagarro.currency.data.remote

import com.nagarro.currency.data.model.HistoricalPriceDTO
import com.nagarro.currency.data.model.LatestRatesDTO
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface PricesAPI {
    @GET("latest")
    suspend fun getLatestPrices(): LatestRatesDTO

    @GET()
    suspend fun getHistoricalPrices(
        @Url
        date: String,
        @Query(value = "symbols", encoded = true)
        symbols: String
    ): HistoricalPriceDTO
}