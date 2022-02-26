package com.nagarro.currency.domain.repository

import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.model.HistoricalExchangeRates
import com.nagarro.currency.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface HistoricalRatesRepository {
    fun getHistoricalRates(from: String, to: String, days: Int): Flow<Result<HistoricalExchangeRates>>
}