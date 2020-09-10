package com.github.jairrab.currencyutility.lib.helpers.apis

abstract class CurrencyRateProvider {
    internal abstract suspend fun getRates(currency1: String, currency2: String): CurrencyRate
}