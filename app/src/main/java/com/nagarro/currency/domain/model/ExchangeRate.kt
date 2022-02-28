package com.nagarro.currency.domain.model

data class ExchangeRate(
    val from: String,
    val to: String,
    val rate: Double
)