package com.github.jairrab.currencyutility.lib.helpers.apis.providers

import android.util.JsonReader
import com.github.jairrab.currencyutility.lib.CurrencyUtilityLibrary.Companion.EXC_RATE
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRateProvider
import timber.log.Timber
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

internal class Fixer(
    private val api: String?,
    private val getHttpConnection: GetHttpConnection,
) : CurrencyRateProvider() {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getRates(currency1: String, currency2: String): CurrencyRate {
        if (api == null) return CurrencyRate()

        var jsonReader: JsonReader? = null
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        return try {
            val eurToCurrency2Rate =  if (currency2.toLowerCase(Locale.US) == "eur") {
                1.0
            } else {
                val url = URL("http://data.fixer.io/latest?access_key=$api&symbols=$currency2&format=1")
                var rate = 0.0

                connection = getHttpConnection.execute(url, false)
                inputStream = connection.inputStream

                jsonReader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
                jsonReader.beginObject()

                while (jsonReader.hasNext()) {
                    val name = jsonReader.nextName()
                    if (name == "rates") {
                        jsonReader.beginObject()
                        while (jsonReader.hasNext()) {
                            val currency = jsonReader.nextName()
                            if (currency == currency2) {
                                rate = jsonReader.nextDouble()
                                Timber.v("$EXC_RATE Converting EUR to $currency2. Http call got $rate")
                            } else {
                                jsonReader.skipValue()
                            }
                        }
                        jsonReader.endObject()
                    } else {
                        jsonReader.skipValue()
                    }
                }

                jsonReader.endObject()
                rate
            }

            if (eurToCurrency2Rate == 0.0) return CurrencyRate()

            if (currency1.toLowerCase(Locale.US) == "eur") {
                CurrencyRate(true, eurToCurrency2Rate)
            } else {
                val eurToCurrency1Rate = getRates("EUR", currency1).rate
                if (eurToCurrency1Rate == 0.0) return CurrencyRate()
                CurrencyRate(true, eurToCurrency2Rate / eurToCurrency1Rate)
            }
        } finally {
            jsonReader?.close()
            inputStream?.close()
            connection?.disconnect()
        }
    }
}