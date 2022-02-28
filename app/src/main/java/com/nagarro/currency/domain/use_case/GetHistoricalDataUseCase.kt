package com.nagarro.currency.domain.use_case

import com.nagarro.currency.domain.model.ExchangeRateRequest
import com.nagarro.currency.domain.model.HistoricalExchangeRates
import com.nagarro.currency.domain.model.Result
import com.nagarro.currency.domain.repository.HistoricalRatesRepository
import com.nagarro.currency.hilt.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistoricalDataUseCase @Inject constructor(
    private val historicalRatesRepository: HistoricalRatesRepository,
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
): FlowUseCase<ExchangeRateRequest, HistoricalExchangeRates>(defaultDispatcher){

    override fun execute(parameters: ExchangeRateRequest): Flow<Result<HistoricalExchangeRates>> {
        return historicalRatesRepository.getHistoricalRates(
            parameters.from,
            parameters.to,
            10
        )
    }
}