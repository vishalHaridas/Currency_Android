package com.nagarro.currency.data.mapper

import com.nagarro.currency.data.model.HistoricalPriceDTO
import com.nagarro.currency.data.model.LatestRatesDTO
import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.model.HistoricalExchangeRates

fun List<HistoricalPriceDTO>.mapToExchangeRate(
    fromSymbol: String,
    toSymbol: String
): HistoricalExchangeRates {

    val historicalRatesMappedToDate: MutableMap<String, String> = mutableMapOf()

    this.forEach { historicalPriceDTO ->
        val exchangeRateOfFromAgainstTo =
            historicalPriceDTO.rates[toSymbol]?.div(historicalPriceDTO.rates[fromSymbol]!!)
        historicalRatesMappedToDate[historicalPriceDTO.date] = exchangeRateOfFromAgainstTo.toString()
    }

    return HistoricalExchangeRates(
        fromSymbol,
        toSymbol,
        historicalRatesMappedToDate
    )
}

fun LatestRatesDTO.mapToListOfCurrencies(): List<Currency> {
    val listOfAvailableCurrencies: ArrayList<Currency> = ArrayList()
    rates.map {
        listOfAvailableCurrencies.add(Currency(it.key, it.value))
    }
    return listOfAvailableCurrencies
}
