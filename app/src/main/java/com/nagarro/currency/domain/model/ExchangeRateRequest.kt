package com.nagarro.currency.domain.model

data class ExchangeRateRequest(
    val from: String,
    val to: String
)