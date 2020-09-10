package com.github.jairrab.currencyutility.lib.helpers.apis

import com.github.jairrab.currencyutility.lib.helpers.GetCurrencyRate
import com.github.jairrab.currencyutility.lib.helpers.apis.providers.*
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetInputStreamString

internal class CryptoApi(
    private val coinCap: CoinCap,
    private val coinGecko: CoinGecko,
    private val eobot: Eobot,
) {
    fun getCryptoRateApis(): List<CurrencyRateProvider> {
        return listOf(
            coinGecko,
            eobot,
            coinCap
        )
    }

    companion object{
        fun getInstance(
            getCurrencyRate: GetCurrencyRate,
            getHttpConnection: GetHttpConnection
        ): CryptoApi {
            return CryptoApi(
                coinCap = CoinCap(getCurrencyRate, getHttpConnection),
                coinGecko = CoinGecko(getHttpConnection),
                eobot = Eobot(getHttpConnection, GetInputStreamString())
            )
        }
    }
}