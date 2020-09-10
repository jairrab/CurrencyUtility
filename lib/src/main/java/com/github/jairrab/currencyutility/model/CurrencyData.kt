package com.github.jairrab.currencyutility.model

class CurrencyData(
    val currency: String,
    val currencyName: String,
    val apiCode: String? = null,
) {
    override fun toString(): String {
        return "[ currency=$currency, currencyName=$currencyName]"
    }
}