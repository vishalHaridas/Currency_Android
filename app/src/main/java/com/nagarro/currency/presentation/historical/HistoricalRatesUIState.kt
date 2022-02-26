package com.nagarro.currency.presentation.historical

import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.model.HistoricalExchangeRates

data class HistoricalRatesUIState(
    val isLoading: Boolean = false,
    val error: Error? = null,
    val data: HistoricalExchangeRates? = null
) {
    sealed class Error{
        object NetworkError: Error()
        object ApiError: Error()
    }
}