package com.nagarro.currency.presentation.convert

import com.nagarro.currency.domain.model.Currency

data class ConvertUIState(
    val isLoading: Boolean = false,
    val error: Error? = null,
    val data: List<Currency>? = null
) {
    sealed class Error{
        object NetworkError: Error()
        object ApiError: Error()
    }
}