package com.nagarro.currency.domain.repository

import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface ConversionRatesRepository {
     fun getLatestRates(): Flow<Result<List<Currency>>>
}