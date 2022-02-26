package com.nagarro.currency.data.model

import com.nagarro.currency.domain.model.Currency

data class LatestRatesDTO(
    val base: String = "",
    val date: String = "",
    val rates: Map<String, Double> = HashMap(),
    val success: Boolean = false,
    val timestamp: Int = -1
)

fun LatestRatesDTO.mapToListOfCurrencies(): List<Currency> {
    val listOfAvailableCurrencies: ArrayList<Currency> = ArrayList()
    rates.map {
        listOfAvailableCurrencies.add(Currency(it.key, it.value))
    }
    return listOfAvailableCurrencies
}