package com.nagarro.currency.data.model

import com.nagarro.currency.domain.model.HistoricalExchangeRates

data class HistoricalPriceDTO(
    val base: String,
    val date: String,
    val historical: Boolean,
    val rates: Map<String, Double>,
    val success: Boolean,
    val timestamp: Int
)

fun List<HistoricalPriceDTO>.mapToExchangeRate(
    fromSymbol: String,
    toSymbol: String
): HistoricalExchangeRates{

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