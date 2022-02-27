package com.nagarro.currency.domain.model

import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class CurrencyTest {

    @Test
    fun `convert returns correct from and to conversion`(){
        val from = Currency("USD", 1.13)
        val to = Currency("AED", 4.14)

        val convertedValue = from.convertTo(to, 1.0)
        assertEquals(convertedValue, 3.66, 0.1)
    }
}