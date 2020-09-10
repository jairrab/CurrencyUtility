package com.github.jairrab.currencyutility.lib.helpers

import com.github.jairrab.currencyutility.lib.CurrencyUtilityLibrary.Companion.EXC_RATE
import com.github.jairrab.currencyutility.lib.helpers.apis.CryptoApi
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import timber.log.Timber

internal class GetCryptoRate(
    private val cryptoApi: CryptoApi,
) {
    suspend fun execute(from: String, to: String): Double {
        val providers = cryptoApi.getCryptoRateApis()

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

        return 1.0
    }

    companion object{
        fun getInstance(
            cryptoApi: CryptoApi,
        ): GetCryptoRate {
            return GetCryptoRate(cryptoApi )
        }
    }
}