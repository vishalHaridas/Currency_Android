package com.nagarro.currency.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(
    val symbol: String = "",
    val rateAgainstEuro: Double = -1.0
) : Parcelable{
    fun convertTo(toCurrency: Currency, value: Double): Double =
      toCurrency.rateAgainstEuro * value / this.rateAgainstEuro
}
