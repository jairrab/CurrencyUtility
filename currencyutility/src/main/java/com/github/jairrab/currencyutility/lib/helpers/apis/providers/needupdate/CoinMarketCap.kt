package com.github.jairrab.currencyutility.lib.helpers.apis.providers.needupdate

import android.util.JsonReader
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRateProvider
import com.github.jairrab.currencyutility.lib.helpers.CryptoHelper
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import java.io.InputStreamReader
import java.net.URL

internal class CoinMarketCap(
    private val getHttpConnection: GetHttpConnection,
) : CurrencyRateProvider() {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getRates(currency1: String, currency2: String): CurrencyRate {
        val cryptoCode = CryptoHelper.getApiCode(currency2)
        val url = URL("https://api.coinmarketcap.com/v1/ticker/$cryptoCode/")
        val connection = getHttpConnection.execute(url, false)
        val inputStream = connection.inputStream

        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        return try {
            var isHasRateFrom = false
            var rateUSD = 1.0

            reader.beginArray()
            reader.beginObject()

            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "price_usd") {
                    isHasRateFrom = true
                    rateUSD = 1 / reader.nextDouble()
                    break
                } else {
                    reader.skipValue()
                }
            }

            if (isHasRateFrom) {
                if (currency1 == "USD") {
                    CurrencyRate(true, rateUSD)
                } else {
                    val usdRate = getRates("USD", currency1).rate
                    CurrencyRate(true, rateUSD / usdRate)
                }
            } else {
                CurrencyRate()
            }
        } finally {
            reader.close()
            inputStream.close()
            connection.disconnect()
        }
    }

    companion object{
        const val API = "2db4446a-52af-41d6-8ae0-3a2558c07da7"
    }
}