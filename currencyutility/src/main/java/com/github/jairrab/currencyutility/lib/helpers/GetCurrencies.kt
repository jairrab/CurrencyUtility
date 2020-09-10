package com.github.jairrab.currencyutility.lib.helpers

import android.os.Build
import com.github.jairrab.currencyutility.model.CurrencyData
import java.util.*

class GetCurrencies {
    fun execute(locale: Locale): List<CurrencyData> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                android.icu.util.Currency.getAvailableCurrencies()
                    .map {
                        val currencyName = it.getDisplayName(locale)
                        val name = if (currencyName == "Philippine Piso") {
                            "Philippine Peso"
                        } else {
                            currencyName
                        }
                        CurrencyData(it.currencyCode, name)
                    }
                    .toMutableList()
                    .apply { addAll(CryptoHelper.cryptoList) }
                    .apply {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            add(CurrencyData("XAU", "Gold (1 Troy Oz)"))
                            add(CurrencyData("XAG", "Silver (1 Troy Oz)"))
                            add(CurrencyData("XPD", "Palladium (1 Troy Oz)"))
                            add(CurrencyData("XPT", "Platinum (1 Troy Oz)"))
                        }
                    }
                    .sortedBy { it.currency }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                Currency.getAvailableCurrencies()
                    .map {
                        val currencyName = it.getDisplayName(locale)
                        val name = if (currencyName == "Philippine Piso") {
                            "Philippine Peso"
                        } else {
                            currencyName
                        }
                        CurrencyData(it.currencyCode, name)
                    }
                    .toMutableList()
                    .apply { addAll(CryptoHelper.cryptoList) }
                    .apply {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            add(CurrencyData("XAU", "Gold (1 Troy Oz)"))
                            add(CurrencyData("XAG", "Silver (1 Troy Oz)"))
                            add(CurrencyData("XPD", "Palladium (1 Troy Oz)"))
                            add(CurrencyData("XPT", "Platinum (1 Troy Oz)"))
                        }
                    }
                    .sortedBy { it.currency }
            }
            else -> {
                Locale.getAvailableLocales()
                    .mapNotNull {
                        try {
                            Currency.getInstance(it).currencyCode
                        } catch (e: Exception) {
                            null
                        }
                    }
                    .distinct()
                    .toMutableList()
                    .apply { addAll(CryptoHelper.cryptoList.map { it.currency }) }
                    .sorted()
                    .map { CurrencyData(it, "") }
            }
        }
    }
}