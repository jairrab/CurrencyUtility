package com.github.jairrab.currencyutility.lib.helpers.apis.providers

import android.util.JsonReader
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRateProvider
import java.io.InputStreamReader
import java.net.URL
import java.util.*

internal class FloatRates(
    private val getHttpConnection: GetHttpConnection,
) : CurrencyRateProvider() {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getRates(currency1: String, currency2: String): CurrencyRate {
        val url = URL(String.format("http://www.floatrates.com/daily/%1\$s.json", currency1))
        val connection = getHttpConnection.execute(url, false)
        val inputStream = connection.inputStream
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        try {
            var rate = 1.0
            reader.beginObject()
            while (reader.hasNext()) {
                val currency = reader.nextName()
                if (currency.toLowerCase(Locale.US) == currency2.toLowerCase(Locale.US)) {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        val name = reader.nextName()
                        if (name == "rate") {
                            rate = reader.nextDouble()
                            return CurrencyRate(true, rate)
                        } else {
                            reader.skipValue()
                        }
                    }
                    reader.endObject()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
            return CurrencyRate(false, rate)
        } finally {
            reader.close()
            inputStream.close()
            connection.disconnect()
        }
    }
}