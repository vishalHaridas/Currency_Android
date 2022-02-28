package com.nagarro.currency.presentation.historical

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nagarro.currency.MainCoroutineRule
import com.nagarro.currency.domain.model.ExchangeRateRequest
import com.nagarro.currency.domain.model.HistoricalExchangeRates
import com.nagarro.currency.domain.model.Result
import com.nagarro.currency.domain.use_case.GetHistoricalDataUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DetailsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineMainRule = MainCoroutineRule()

    @RelaxedMockK
    lateinit var useCase: GetHistoricalDataUseCase

    private lateinit var viewModel: DetailsViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = DetailsViewModel(useCase)
    }


    @Test
    fun `fetch data with success returns a list of data`() = runBlocking {
        every {
            useCase(ExchangeRateRequest("USD", "AED"))
        } returns flowOf(
            Result.Success(
                HistoricalExchangeRates(
                    fromCurrency = "USD",
                    toCurrency = "AED",
                    historicalRates = mapOf(
                        "date1" to "rate",
                        "date2" to "rate"
                    )
                )
            )
        )
        viewModel.fetchData(ExchangeRateRequest("USD", "AED"))
        val hasData = viewModel.historicalDataUIState.value.data?.historicalRates?.isNotEmpty()!!
        assertTrue(hasData)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}