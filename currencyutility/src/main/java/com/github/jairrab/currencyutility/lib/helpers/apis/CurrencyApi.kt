package com.github.jairrab.currencyutility.lib.helpers.apis

import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetHttpConnection
import com.github.jairrab.currencyutility.lib.helpers.apis.http.GetInputStreamString
import com.github.jairrab.currencyutility.lib.helpers.apis.providers.*
import com.github.jairrab.currencyutility.model.ApiKeys

internal class CurrencyApi(
    private val currencyConverterApi: CurrencyConverterApi,
    private val currencyLayer: CurrencyLayer,
    private val eobot: Eobot,
    private val fixer: Fixer,
    private val floatRates: FloatRates,
    private val openExchange: OpenExchange,
) {
    fun getCurrencyRateApis(): List<CurrencyRateProvider> {
        return listOf(
            eobot,
            floatRates
        )
    }

    fun getCurrencyRateApisRandom(): List<CurrencyRateProvider> {
        return listOf(
            currencyConverterApi,
            currencyLayer,
            fixer,
            openExchange,
        )
    }

    companion object {
        fun getInstance(
            apiKeys: ApiKeys,
            getHttpConnection: GetHttpConnection
        ) = CurrencyApi(
            currencyConverterApi = CurrencyConverterApi(apiKeys.currencyConverterApi),
            currencyLayer = CurrencyLayer(apiKeys.currencyLayer, getHttpConnection),
            eobot = Eobot(getHttpConnection, GetInputStreamString()),
            fixer = Fixer(apiKeys.fixer, getHttpConnection),
            floatRates = FloatRates(getHttpConnection),
            openExchange = OpenExchange(apiKeys.openExchange, getHttpConnection)
        )
    }
}
