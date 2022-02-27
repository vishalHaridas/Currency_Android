package com.nagarro.currency.presentation.convert

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nagarro.currency.MainCoroutineRule
import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.model.Result
import com.nagarro.currency.domain.use_case.GetAvailableRatesUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.Error
import java.lang.Exception

@RunWith(JUnit4::class)
class ConvertViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineMainRule = MainCoroutineRule()

    @RelaxedMockK
    lateinit var useCase: GetAvailableRatesUseCase

    private lateinit var viewModel: ConvertViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = ConvertViewModel(useCase)
    }


    @Test
    fun `fetch data with success returns a list of data`() = runBlocking {
        getMockSuccessCurrencies()
        viewModel.fetchData()
        val hasData = viewModel.availableCurrenciesState.value.data?.size!! > 0
        assertTrue(hasData)
    }

    private fun getMockSuccessCurrencies() {
        every {
            useCase(null)
        } returns flowOf(
            Result.Success(
                listOf(
                    Currency("USD", 1.13),
                    Currency("AED", 4.14)
                )
            )
        )
    }

    @Test
    fun `fetch data empty list gives an API error`() = runBlocking {
        every {
            useCase(null)
        } returns flowOf(
            Result.Success(listOf())
        )
        viewModel.fetchData()
        assertEquals(viewModel.availableCurrenciesState.value, ConvertUIState(error = ConvertUIState.Error.ApiError))
    }


    @After
    fun tearDown() {
        unmockkAll()
    }

}