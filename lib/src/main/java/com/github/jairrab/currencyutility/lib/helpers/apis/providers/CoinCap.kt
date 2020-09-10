package com.github.jairrab.currencyutility.lib.helpers.apis.providers

import android.util.JsonReader
import com.github.jairrab.currencyutility.lib.CurrencyUtilityLibrary.Companion.EXC_RATE
import com.github.jairrab.currencyutility.lib.helpers.CryptoHelper
import com.github.jairrab.currencyutility.lib.helpers.GetCurrencyRate
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRateProvider
import timber.log.Timber
import java.io.InputStreamReader
import java.net.URL
import java.util.*

internal class CoinCap(
    private val getCurrencyRate: GetCurrencyRate,
    private val getHttpConnection: GetHttpConnection,
) : CurrencyRateProvider() {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getRates(currency1: String, currency2: String): CurrencyRate {
        val cryptoCode = CryptoHelper.getApiCode(currency2)
        val url = URL("https://api.coincap.io/v2/assets/$cryptoCode")
        val connection = getHttpConnection.execute(url, false)
        val inputStream = connection.inputStream

        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        return try {
            var usdToCurrency2 = 0.0

            reader.beginObject()
            reader.nextName()
            reader.beginObject()

            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "priceUsd") {
                    usdToCurrency2 = 1 / reader.nextDouble()
                    Timber.v("$EXC_RATE Converting USD to $currency2. Http call got $usdToCurrency2")
                    break
                } else {
                    reader.skipValue()
                }
            }

            if (usdToCurrency2 == 0.0) return CurrencyRate()

            if (currency1.toLowerCase(Locale.US) == "usd") {
                CurrencyRate(true, usdToCurrency2)
            } else {
                val usdToCurrency1 = getCurrencyRate.execute("USD", currency1)
                if (usdToCurrency1 == 0.0) return CurrencyRate()
                CurrencyRate(true, usdToCurrency2 / usdToCurrency1)
            }
        } finally {
            reader.close()
            inputStream.close()
            connection.disconnect()
        }
    }
}