package com.github.jairrab.currencyutility.lib.helpers.apis.http

import java.net.HttpURLConnection
import java.net.URL

class GetHttpConnection() {
    fun execute(url: URL, isForceRefresh: Boolean): HttpURLConnection {
        val connection = url.openConnection() as HttpURLConnection
        connection.readTimeout = 7000
        connection.connectTimeout = 15000
        connection.requestMethod = "GET"
        connection.doInput = true
        if (isForceRefresh) {
            connection.addRequestProperty("Cache-Control", "no-cache")
        }
        connection.connect()
        return connection
    }
}