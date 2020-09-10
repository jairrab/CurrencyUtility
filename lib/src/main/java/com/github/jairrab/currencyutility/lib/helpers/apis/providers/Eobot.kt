package com.github.jairrab.currencyutility.lib.helpers.apis.providers

import com.github.jairrab.currencyutility.lib.CurrencyUtilityLibrary.Companion.EXC_RATE
import com.github.jairrab.currencyutility.lib.helpers.CryptoHelper
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetInputStreamString
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRate
import com.github.jairrab.currencyutility.lib.helpers.apis.CurrencyRateProvider
import timber.log.Timber
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

internal class Eobot(
    private val getHttpConnection: GetHttpConnection,
    private val getInputStreamString: GetInputStreamString,
) : CurrencyRateProvider() {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getRates(currency1: String, currency2: String): CurrencyRate {
        var inputStream: InputStream? = null
        var connection: HttpURLConnection? = null
        return try {
            val rateDouble = if (currency2.toLowerCase(Locale.US) == "usd") {
                1.0
            } else {
                val url = URL("https://www.eobot.com/api.aspx?coin=$currency2")
                connection = getHttpConnection.execute(url, false)
                inputStream = connection.inputStream
                val rateString = getInputStreamString.execute(inputStream)
                val rate = rateString.parseDoubleString()
                Timber.v("$EXC_RATE Converting USD to $currency2. Http call got $rate")
                rate
            }

            if (rateDouble == 0.0) return CurrencyRate()

            val usdToCurrency2Rate = if (CryptoHelper.isCryptoCurrency(currency2)) {
                1 / rateDouble
            } else rateDouble

            if (currency1.toLowerCase(Locale.US) == "usd") {
                CurrencyRate(true, usdToCurrency2Rate)
            } else {
                val usdToCurrency1Rate = getRates("USD", currency1).rate
                if (usdToCurrency1Rate == 0.0) return CurrencyRate()
                CurrencyRate(true, usdToCurrency2Rate / usdToCurrency1Rate)
            }
        } finally {
            inputStream?.close()
            connection?.disconnect()
        }
    }

    private fun String.parseDoubleString(): Double {
        return try {
            this.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }
}