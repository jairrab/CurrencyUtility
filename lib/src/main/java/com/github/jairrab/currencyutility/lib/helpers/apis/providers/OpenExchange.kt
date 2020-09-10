package com.github.jairrab.currencyutility.lib.helpers.apis.providers

import android.util.JsonReader
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRateProvider
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import java.io.InputStreamReader
import java.net.URL

internal class OpenExchange(
    private val api: String?,
    private val getHttpConnection: GetHttpConnection,
) : CurrencyRateProvider() {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getRates(currency1: String, currency2: String): CurrencyRate {
        if (api == null) return CurrencyRate()

        val url = URL("https://openexchangerates.org/api/latest.json?app_id=$api&symbols=$currency1,$currency2")
        val connection = getHttpConnection.execute(url, false)
        val inputStream = connection.inputStream
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        return try {
            var isHasRateFrom = false
            var isHasRateTo = false
            var rateFrom = 1.0
            var rateTo = 1.0

            reader.beginObject()

            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "rates") {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        val currency = reader.nextName()
                        if (currency == currency1) {
                            isHasRateFrom = true
                            rateFrom = reader.nextDouble()
                        } else if (currency == currency2) {
                            isHasRateTo = true
                            rateTo = reader.nextDouble()
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

            if (isHasRateFrom && isHasRateTo) {
                CurrencyRate(true, rateTo / rateFrom)
            } else {
                CurrencyRate()
            }
        } finally {
            reader.close()
            inputStream.close()
            connection.disconnect()
        }
    }
}