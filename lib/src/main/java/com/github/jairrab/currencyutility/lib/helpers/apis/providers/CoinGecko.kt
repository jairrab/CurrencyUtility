package com.github.jairrab.currencyutility.lib.helpers.apis.providers

import android.util.JsonReader
import com.github.jairrab.currencyutility.lib.CurrencyUtilityLibrary.Companion.EXC_RATE
import com.github.jairrab.currencyutility.lib.helpers.CryptoHelper
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRateProvider
import timber.log.Timber
import java.io.InputStreamReader
import java.net.URL

internal class CoinGecko(
    private val getHttpConnection: GetHttpConnection,
) : CurrencyRateProvider() {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getRates(currency1: String, currency2: String): CurrencyRate {
        val cryptoCode = CryptoHelper.getApiCode(currency2)
        val url = URL("https://api.coingecko.com/api/v3/simple/price?ids=$cryptoCode&vs_currencies=$currency1")
        val connection = getHttpConnection.execute(url, false)
        val inputStream = connection.inputStream

        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        return try {
            reader.beginObject()
            reader.nextName()
            reader.beginObject()
            reader.nextName()

            val rate = 1 / reader.nextDouble()

            Timber.v("$EXC_RATE Converting $currency1 to $currency2. Http call got $rate")

            if (rate == 0.0) return CurrencyRate()

            CurrencyRate(true, rate)
        } catch (e: Exception) {
            Timber.e("$EXC_RATE ${e.message}")
            throw e
        } finally {
            reader.close()
            inputStream.close()
            connection.disconnect()
        }
    }
}