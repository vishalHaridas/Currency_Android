package com.nagarro.currency.domain.model

data class HistoricalExchangeRates(
    var fromCurrency: String = "",
    var toCurrency: String = "",
    var historicalRates: Map<String, String> = HashMap()
)