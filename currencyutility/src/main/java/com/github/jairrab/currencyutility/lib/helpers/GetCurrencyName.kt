package com.github.jairrab.currencyutility.lib.helpers

import android.icu.util.Currency
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import java.util.*

class GetCurrencyName {
    fun execute(currency: String): String {
        val name = getCryptoCurrencyName(currency)
        if (name != null) return name

        val defaultLocale = if (SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Locale.forLanguageTag(Locale.getDefault().language)
        } else {
            Locale.getDefault()
        }

        return when {
            SDK_INT >= VERSION_CODES.N -> {
                try {
                    val cur = Currency.getInstance(currency)
                    if (cur != null) {
                        val displayName = cur.getDisplayName(defaultLocale)
                        if (displayName == "Philippine Piso") "Philippine Peso" else displayName
                    } else ""
                } catch (e: Exception) {
                    ""
                }
            }
            SDK_INT >= VERSION_CODES.KITKAT -> {
                try {
                    val cur = java.util.Currency.getInstance(currency)
                    if (cur != null) {
                        val displayName = cur.getDisplayName(defaultLocale)
                        if (displayName == "Philippine Piso") "Philippine Peso" else displayName
                    } else ""
                } catch (e: Exception) {
                    ""
                }
            }
            else -> currency
        }
    }

    private fun getCryptoCurrencyName(currency: String) =
        CryptoHelper.cryptoList.firstOrNull { it.currency == currency }?.currencyName
}