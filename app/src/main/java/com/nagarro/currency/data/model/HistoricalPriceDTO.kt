package com.nagarro.currency.data.model

data class HistoricalPriceDTO(
    val base: String,
    val date: String,
    val historical: Boolean,
    val rates: Map<String, Double>,
    val success: Boolean,
    val timestamp: Int
)