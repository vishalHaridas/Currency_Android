package com.nagarro.currency.hilt

import com.nagarro.currency.data.remote.PricesAPI
import com.nagarro.currency.data.repository.ConversionRatesRepositoryImpl
import com.nagarro.currency.data.repository.HistoricalRatesRepositoryImpl
import com.nagarro.currency.domain.repository.ConversionRatesRepository
import com.nagarro.currency.domain.repository.HistoricalRatesRepository
import com.nagarro.currency.domain.use_case.GetAvailableRatesUseCase
import com.nagarro.currency.domain.use_case.GetHistoricalDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModules {

    @Singleton
    @Provides
    fun provideLatestRatesRepository(
        pricesAPI: PricesAPI,
    ): ConversionRatesRepository =
        ConversionRatesRepositoryImpl(pricesAPI)

    @Singleton
    @Provides
    fun provideGetSupportedPricesUseCase(
        conversionRatesRepository: ConversionRatesRepository,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): GetAvailableRatesUseCase =
        GetAvailableRatesUseCase(conversionRatesRepository, defaultDispatcher)


    @Singleton
    @Provides
    fun provideHistoricalRatesRepository(
        pricesAPI: PricesAPI
    ): HistoricalRatesRepository =
        HistoricalRatesRepositoryImpl(pricesAPI)

    @Singleton
    @Provides
    fun provideGetHistoricalRatesUseCase(
        historicalRatesRepository: HistoricalRatesRepository,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): GetHistoricalDataUseCase =
        GetHistoricalDataUseCase(historicalRatesRepository, defaultDispatcher)

}