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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private val _performSpinnerSwap: MutableSharedFlow<String> = MutableSharedFlow()
    val performSwap = _performSpinnerSwap.asSharedFlow()

    private val _popularCurrencies: MutableStateFlow<List<Currency>> = MutableStateFlow(mutableListOf())
    val popularCurrencies = _popularCurrencies.asStateFlow()

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
                    populatePopularCurrencies()
                } catch (e: Exception) {
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

    private fun populatePopularCurrencies() {
        val popularCurrencies: List<Currency>
        val popularCurrencySymbols = listOf(
            "USD",
            "AED",
            "EUR",
            "HKD",
            "EGP",
            "AUD",
            "INR",
            "JPY",
            "RUB",
            "GBP"
        )

        popularCurrencies= _uiState.value.data?.filter {
            it.symbol in popularCurrencySymbols
        }!!

        _popularCurrencies.value = popularCurrencies

        Log.d("Convert", "val is: $popularCurrencies")
    }

    fun swapPriceValues() {
        ensureHasValue(fromPrice)
        ensureHasValue(toPrice)

        viewModelScope.launch {
            _performSpinnerSwap.emit("swap")
        }

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
        updateTo()
    }

    fun updateFrom() {
        ensureHasValue(toPrice)
        val convertedAmount =
            selectedToCurrency.convertTo(selectedFromCurrency, toPrice.value.toDouble())
        fromPrice.value = String.format("%.2f", convertedAmount)
    }


}