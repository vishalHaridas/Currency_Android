package com.nagarro.currency.presentation.historical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.model.ExchangeRate
import com.nagarro.currency.domain.model.ExchangeRateRequest
import com.nagarro.currency.domain.use_case.GetHistoricalDataUseCase
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
class DetailsViewModel @Inject constructor(
    val historicalDataUseCase: GetHistoricalDataUseCase
) : ViewModel() {


    private val _popularExchangeRates: MutableStateFlow<List<ExchangeRate>> =
        MutableStateFlow(listOf())
    val popularExchangeRates = _popularExchangeRates.asStateFlow()

    val isDataFetched: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _uiState: MutableStateFlow<HistoricalRatesUIState> =
        MutableStateFlow(HistoricalRatesUIState())
    val historicalDataUIState = _uiState.asStateFlow()

    fun fetchData(request: ExchangeRateRequest) {
        if (!isDataFetched.value)
            historicalDataUseCase(request)
                .onLoading {
                    _uiState.value = HistoricalRatesUIState(isLoading = true)
                }
                .onSuccess { historicalRates ->
                    try {
                        _uiState.value = HistoricalRatesUIState(data = historicalRates)
                    }catch (e: Exception){
                        _uiState.value = HistoricalRatesUIState(error = HistoricalRatesUIState.Error.ApiError)
                    }
                    isDataFetched.value = true
                }
                .onError { e ->
                    when (e.exception) {
                        is IOException -> _uiState.value =
                            HistoricalRatesUIState(error = HistoricalRatesUIState.Error.NetworkError)
                    }
                }
                .launchIn(viewModelScope)
    }

    fun calculatePopularCurrencies(from: Currency, popularCurrencies: List<Currency>) {
        val popularExchangeRates = mutableListOf<ExchangeRate>()
        popularCurrencies.forEach { currency ->
            val convertedAmount = from.convertTo(currency, 1.0)
            popularExchangeRates.add(
                ExchangeRate(
                    from.symbol,
                    currency.symbol,
                    convertedAmount
                )
            )
        }

        _popularExchangeRates.value = popularExchangeRates
    }
}