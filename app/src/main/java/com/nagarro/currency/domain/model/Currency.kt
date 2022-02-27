package com.nagarro.currency.domain.model

data class Currency(
    val symbol: String = "",
    //val name: String = ""
    val rateAgainstEuro: Double = -1.0
){
    fun convertTo(toCurrency: Currency, value: Double): Double =
      toCurrency.rateAgainstEuro * value / this.rateAgainstEuro
}
