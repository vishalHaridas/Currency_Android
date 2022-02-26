package com.nagarro.currency.data

import java.io.IOException

class NoConnectivityException: IOException() {
    override val message: String?
        get() = "No Internet, please check your connection"
}