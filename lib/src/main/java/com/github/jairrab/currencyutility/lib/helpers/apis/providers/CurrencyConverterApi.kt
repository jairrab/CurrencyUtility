package com.github.jairrab.currencyutility.lib.helpers.apis.providers

import android.util.JsonReader
import com.github.jairrab.currencyutility.lib.CurrencyUtilityLibrary.Companion.EXC_RATE
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRateProvider
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import timber.log.Timber
import java.io.InputStreamReader
import java.net.URL

internal class CurrencyConverterApi(
    private val api: String?
) : CurrencyRateProvider() {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getRates(currency1: String, currency2: String): CurrencyRate {
        if (api == null) return CurrencyRate()

        val url = URL("https://free.currconv.com/api/v7/convert?q=${currency1}_$currency2&compact=ultra&apiKey=$api")
        val connection = GetHttpConnection().execute(url, false)

        val inputStream = connection.inputStream
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        return try {
            var rate = 0.0

            reader.beginObject()

            if (reader.hasNext()) {
                reader.nextName()
                rate = reader.nextDouble()
                Timber.v("$EXC_RATE Converting $currency1 to $currency2. Http call got $rate")
            }

            reader.endObject()

            if (rate == 0.0) return CurrencyRate()

            CurrencyRate(true, rate)

        } finally {
            reader.close()
            inputStream.close()
            connection.disconnect()
        }
    }
}