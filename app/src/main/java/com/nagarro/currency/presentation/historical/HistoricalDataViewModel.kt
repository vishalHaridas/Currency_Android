package com.nagarro.currency.presentation.historical

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagarro.currency.domain.model.ExchangeRateRequest
import com.nagarro.currency.domain.use_case.GetHistoricalDataUseCase
import com.nagarro.currency.domain.util.extension.onError
import com.nagarro.currency.domain.util.extension.onLoading
import com.nagarro.currency.domain.util.extension.onSuccess
import com.nagarro.currency.presentation.convert.ConvertUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import java.io.IOException
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class HistoricalDataViewModel @Inject constructor(
    val historicalDataUseCase: GetHistoricalDataUseCase
): ViewModel() {

    private val TAG = "HistoricalViewModel"

    private val _uiState: MutableStateFlow<HistoricalRatesUIState> = MutableStateFlow(HistoricalRatesUIState())
    val historicalDataUIState = _uiState.asStateFlow()

    fun fetchData(request: ExchangeRateRequest) {
        historicalDataUseCase(request)
            .onLoading {
                Log.d(TAG, "historical data is loading...")

                _uiState.value = HistoricalRatesUIState(isLoading = true)
            }
            .onSuccess { historicalRates ->
                Log.d(TAG, "data is: $historicalRates")
                _uiState.value = HistoricalRatesUIState(data = historicalRates)
            }
            .onError { e ->
                Log.e(TAG, "Error is: $e")
                when(e.exception){
                    is IOException -> _uiState.value = HistoricalRatesUIState(error = HistoricalRatesUIState.Error.NetworkError)
                }
            }
            .launchIn(viewModelScope)
    }
}