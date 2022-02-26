package com.nagarro.currency.data.repository

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.util.Log
import com.nagarro.currency.data.model.HistoricalPriceDTO
import com.nagarro.currency.data.model.mapToExchangeRate
import com.nagarro.currency.data.remote.PricesAPI
import com.nagarro.currency.domain.model.HistoricalExchangeRates
import com.nagarro.currency.domain.model.Result
import com.nagarro.currency.domain.repository.HistoricalRatesRepository
import com.nagarro.currency.domain.util.extension.resultFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import javax.inject.Inject

class HistoricalRatesRepositoryImpl @Inject constructor(
    private val pricesAPI: PricesAPI
): HistoricalRatesRepository {
    override fun getHistoricalRates(from: String, to: String, days: Int):
            Flow<Result<HistoricalExchangeRates>> {
        return resultFlow {
            val result = getList(from, to, days)
            return@resultFlow result.mapToExchangeRate(from,to)
        }
    }

    private suspend fun getList(from: String, to: String, days: Int): List<HistoricalPriceDTO>{
        val listOfHistoricalPriceDTO: MutableList<HistoricalPriceDTO> = mutableListOf()
        withContext(Dispatchers.IO){
            for (day in 1 until days){
                Log.d("HistoricalViewModel", "date: ${getDateDaysBefore(day)}")
                val result = async {
                    pricesAPI.getHistoricalPrices(
                        getDateDaysBefore(day),
                        "$from,$to"
                    )
                }.await()
                listOfHistoricalPriceDTO.add(result)
            }
        }

        return listOfHistoricalPriceDTO
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateDaysBefore(day: Int): String{
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        calendar.add(Calendar.DAY_OF_YEAR, -day)
        return dateFormat.format(calendar.time)
    }

}