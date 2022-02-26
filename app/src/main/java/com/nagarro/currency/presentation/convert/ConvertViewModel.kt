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
import javax.inject.Inject

@HiltViewModel
class ConvertViewModel @Inject constructor(
    val availableRatesUseCase: GetAvailableRatesUseCase,
) : ViewModel() {

    private val TAG = "ConvertViewModel"

    private lateinit var selectedFromCurrency: Currency
    private lateinit var selectedToCurrency: Currency

    private val _uiState: MutableStateFlow<ConvertUIState> = MutableStateFlow(ConvertUIState())
    val availableCurrenciesState = _uiState.asStateFlow()

    val fromPrice: MutableStateFlow<String> = MutableStateFlow("1")
    val toPrice: MutableStateFlow<String> = MutableStateFlow("1")

    init {
        fetchData()
    }

    private fun fetchData() {
        Log.d(TAG, "fetch data called")

        availableRatesUseCase("")
            .onLoading {
                Log.d(TAG, "supportedPrice is loading")

                _uiState.value = ConvertUIState(isLoading = true)
            }
            .onSuccess { currencyList ->
                Log.d(TAG, "data is: $currencyList")
                selectedFromCurrency = currencyList[0]
                selectedToCurrency = currencyList[0]

                _uiState.value = ConvertUIState(data = currencyList)
            }
            .onError { e ->
                when(e.exception){
                  is IOException -> _uiState.value = ConvertUIState(error = ConvertUIState.Error.NetworkError)
                }
            }
            .launchIn(viewModelScope)

    }

    //write tests
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
        val tempPrice: String = toPrice.value!!
        toPrice.value = fromPrice.value
        fromPrice.value = tempPrice
    }

    private fun ensureHasValue(priceValue: MutableStateFlow<String>) {
        if (priceValue.value.isEmpty())
            priceValue.value = "1"
    }

    //init with from to  to price conversion
    //cases:
    //  from has focus
    //  to has focus
    //  neither has focus

    fun updateTo() {
        ensureHasValue(fromPrice)
        val convertedAmount = selectedFromCurrency.convertTo(selectedToCurrency, fromPrice.value.toDouble())
        toPrice.value = String.format("%.2f", convertedAmount)
    }

    fun setFromCurrency(currency: Currency){
        selectedFromCurrency = currency
        updateTo()
    }

    fun setToCurrency(currency: Currency){
        selectedToCurrency = currency
        updateFrom()
    }

    fun updateFrom() {
        ensureHasValue(toPrice)
        val convertedAmount = selectedToCurrency.convertTo(selectedFromCurrency, toPrice.value.toDouble())
        fromPrice.value = String.format("%.2f", convertedAmount)
    }


}