package com.nagarro.currency.data.repository

import android.util.Log
import com.nagarro.currency.data.model.mapToListOfCurrencies
import com.nagarro.currency.data.remote.PricesAPI
import com.nagarro.currency.domain.util.extension.resultFlow
import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.model.Result
import com.nagarro.currency.domain.repository.ConversionRatesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConversionRatesRepositoryImpl @Inject constructor(
    private val pricesAPI: PricesAPI,
) : ConversionRatesRepository {

    override fun getLatestRates(): Flow<Result<List<Currency>>> = resultFlow {
            val supportedRates = pricesAPI.getLatestPrices()
            return@resultFlow supportedRates.mapToListOfCurrencies()
        }
}