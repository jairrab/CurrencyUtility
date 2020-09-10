package com.github.jairrab.currencyutility.lib

import com.github.jairrab.currencyutility.CurrencyUtility
import com.github.jairrab.currencyutility.lib.helpers.CryptoHelper
import com.github.jairrab.currencyutility.lib.helpers.GetCurrencies
import com.github.jairrab.currencyutility.lib.helpers.GetCurrencyName
import com.github.jairrab.currencyutility.lib.helpers.GetCryptoRate
import com.github.jairrab.currencyutility.lib.helpers.GetCurrencyRate
import com.github.jairrab.currencyutility.model.CurrencyData
import com.jairrab.github.currencysymbols.CurrencySymbols
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