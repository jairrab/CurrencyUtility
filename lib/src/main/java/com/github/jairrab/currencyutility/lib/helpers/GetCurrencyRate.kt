package com.github.jairrab.currencyutility.lib.helpers

import com.github.jairrab.currencyutility.lib.CurrencyUtilityLibrary.Companion.EXC_RATE
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyApi
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import timber.log.Timber
import java.io.IOException

internal class GetCurrencyRate(
    private val currencyApi: CurrencyApi,
) {
    suspend fun execute(from: String, to: String): Double {
        val providers = currencyApi.getCurrencyRateApis()

        for (provider in providers) {
            var currencyRate: CurrencyRate? = null
            try {
                currencyRate = provider.getRates(from, to)
            } catch (e: Exception) {
                Timber.e(e)
            }
            if (currencyRate?.isHasRate == true) {
                val rate = currencyRate.rate
                Timber.v("$EXC_RATE $from to $to rate = $rate, from ${provider.javaClass.simpleName}")
                return if (rate == 0.0) 1.0 else rate
            }
        }

        return getRandom(from, to)
    }

    private suspend fun getRandom(from: String, to: String): Double {
        Timber.v("$EXC_RATE Getting from random providers")
        val randomProviders = currencyApi.getCurrencyRateApisRandom().toMutableList()
        randomProviders.shuffle()

        for (provider in randomProviders) {
            Timber.v("$EXC_RATE Getting from ${provider.javaClass.simpleName}...")
            var currencyRate: CurrencyRate? = null
            try {
                currencyRate = provider.getRates(from, to)
            } catch (e: IOException) {
                Timber.e(e)
            }
            if (currencyRate?.isHasRate == true) {
                val rate = currencyRate.rate
                Timber.v("$EXC_RATE $from to $to rate = $rate, from ${provider.javaClass.simpleName}")
                return if (rate == 0.0) 1.0 else rate
            }
        }

        return 0.0
    }

    companion object {
        fun getInstance(currencyApi: CurrencyApi): GetCurrencyRate {
            return GetCurrencyRate(currencyApi)
        }
    }
}