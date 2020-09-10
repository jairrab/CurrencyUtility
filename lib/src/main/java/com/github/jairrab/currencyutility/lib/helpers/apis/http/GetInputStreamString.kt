package com.github.jairrab.currencyutility.lib.helpers.apis.http

import java.io.InputStream
import java.util.*

class GetInputStreamString() {
    /**
     * Reads an InputStream and converts it to a String.
     *
     * @param stream InputStream to read
     * @param length length of inputStream buffer to convert to String
     * @return Converted InputStream to String
     */
    fun execute(stream: InputStream?): String {
        val s: Scanner = Scanner(stream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }
}