package com.github.jairrab.currencyutility

import com.github.jairrab.currencyutility.lib.CurrencyUtilityLibrary
import com.github.jairrab.currencyutility.lib.helpers.GetCryptoRate
import com.github.jairrab.currencyutility.lib.helpers.GetCurrencies
import com.github.jairrab.currencyutility.lib.helpers.GetCurrencyName
import com.github.jairrab.currencyutility.lib.helpers.GetCurrencyRate
import com.github.jairrab.currencyutility.model.ApiKeys
import com.github.jairrab.currencyutility.lib.helpers.apis.CryptoApi
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyApi
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import com.github.jairrab.currencyutility.model.CurrencyData
import java.util.*

interface CurrencyUtility {
    fun getCurrencyName(currency: String): String
    fun getCurrencySymbol(currency: String): String
    fun getCurrencies(locale: Locale): List<CurrencyData>
    suspend fun getCurrencyRate(from: String, to: String): Double

    companion object {
        fun getInstance(apiKeys: ApiKeys): CurrencyUtility {
            val getHttpConnection = GetHttpConnection()
            val currencyApi = CurrencyApi.getInstance(apiKeys, getHttpConnection)
            val getCurrencyRate = GetCurrencyRate.getInstance(currencyApi)
            val cryptoApi = CryptoApi.getInstance(getCurrencyRate, getHttpConnection)
            val getCryptoRate = GetCryptoRate.getInstance(cryptoApi)
            val getCurrencyName = GetCurrencyName()
            val getCurrencies = GetCurrencies()

            return CurrencyUtilityLibrary(
                getCurrencyName = getCurrencyName,
                getCurrencies = getCurrencies,
                getCryptoRate = getCryptoRate,
                getCurrencyRate = getCurrencyRate
            )
        }
    }
}

