package com.nagarro.currency.presentation.convert

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.use_case.GetAvailableRatesUseCase
import com.nagarro.currency.domain.util.extension.onError
import com.nagarro.currency.domain.util.extension.onLoading
import com.nagarro.currency.domain.util.extension.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ConvertViewModel @Inject constructor(
    val availableRatesUseCase: GetAvailableRatesUseCase,
) : ViewModel() {

    lateinit var selectedFromCurrency: Currency
    lateinit var selectedToCurrency: Currency

    private val _uiState: MutableStateFlow<ConvertUIState> = MutableStateFlow(ConvertUIState())
    val availableCurrenciesState = _uiState.asStateFlow()

    val fromPrice: MutableStateFlow<String> = MutableStateFlow("1")
    val toPrice: MutableStateFlow<String> = MutableStateFlow("1")

    fun fetchData() {
        availableRatesUseCase(null)
            .onLoading {
                _uiState.value = ConvertUIState(isLoading = true)
            }
            .onSuccess { currencyList ->
                try {
                    val firstCurrency = currencyList[0]
                    selectedFromCurrency = firstCurrency
                    selectedToCurrency = firstCurrency
                    _uiState.value = ConvertUIState(data = currencyList)
                } catch (e: Exception){
                    _uiState.value = ConvertUIState(error = ConvertUIState.Error.ApiError)
                }
            }
            .onError { e ->
                when (e.exception) {
                    is IOException -> _uiState.value =
                        ConvertUIState(error = ConvertUIState.Error.NetworkError)
                }
            }
            .launchIn(viewModelScope)

    }

    fun swapPriceValues() {
        ensureHasValue(fromPrice)
        ensureHasValue(toPrice)

        performSwap()
    }

    fun ensureFromHasValue() {
        ensureHasValue(fromPrice)
    }

    fun ensureToHasValue() {
        ensureHasValue(toPrice)
    }

    private fun performSwap() {
        val tempPrice: String = toPrice.value
        toPrice.value = fromPrice.value
        fromPrice.value = tempPrice
    }

    private fun ensureHasValue(priceValue: MutableStateFlow<String>) {
        if (priceValue.value.isEmpty())
            priceValue.value = "1"
    }

    fun updateTo() {
        ensureHasValue(fromPrice)
        val convertedAmount =
            selectedFromCurrency.convertTo(selectedToCurrency, fromPrice.value.toDouble())
        toPrice.value = String.format("%.2f", convertedAmount)
    }

    fun setFromCurrency(currency: Currency) {
        selectedFromCurrency = currency
        updateTo()
    }

    fun setToCurrency(currency: Currency) {
        selectedToCurrency = currency
        updateFrom()
    }

    fun updateFrom() {
        ensureHasValue(toPrice)
        val convertedAmount =
            selectedToCurrency.convertTo(selectedFromCurrency, toPrice.value.toDouble())
        fromPrice.value = String.format("%.2f", convertedAmount)
    }


}