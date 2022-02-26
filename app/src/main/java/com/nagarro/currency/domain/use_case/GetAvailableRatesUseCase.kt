package com.nagarro.currency.domain.use_case

import com.nagarro.currency.domain.model.Currency
import com.nagarro.currency.domain.model.Result
import com.nagarro.currency.domain.repository.ConversionRatesRepository
import com.nagarro.currency.hilt.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAvailableRatesUseCase @Inject constructor(
    private val conversionRatesRepository: ConversionRatesRepository,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
): FlowUseCase<String, List<Currency>>(defaultDispatcher) {

    override fun execute(parameters: String): Flow<Result<List<Currency>>> {
        return conversionRatesRepository.getLatestRates()
    }
}