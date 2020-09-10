package com.github.jairrab.currencyutility.lib

import com.github.jairrab.currencysymbols.CurrencySymbols
import com.github.jairrab.currencyutility.CurrencyUtility
import com.github.jairrab.currencyutility.lib.helpers.*
import com.github.jairrab.currencyutility.model.CurrencyData
import java.util.*

internal class CurrencyUtilityLibrary(
    private val getCurrencyName: GetCurrencyName,
    private val getCurrencies: GetCurrencies,
    private val getCryptoRate: GetCryptoRate,
    private val getCurrencyRate: GetCurrencyRate,
) : CurrencyUtility {
    override fun getCurrencyName(currency: String): String {
        return getCurrencyName.execute(currency)
    }

    override fun getCurrencySymbol(currency: String): String {
        return CurrencySymbols.get(currency)
    }

    override fun getCurrencies(locale: Locale): List<CurrencyData> {
        return getCurrencies.execute(locale)
    }

    override suspend fun getCurrencyRate(from: String, to: String): Double {
        return if (CryptoHelper.isCryptoCurrency(to)) {
            getCryptoRate.execute(from, to)
        } else {
            getCurrencyRate.execute(from, to)
        }
    }

    companion object{
        const val EXC_RATE = "~~~~"
    }
}