package io.github.darefox.hltbproxy.scraping

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Parse until success or end of list
 *
 * @throws IllegalArgumentException if array is empty
 * @throws ParseException if all parsers failed
 */
internal fun List<SimpleDateFormat>.parse(string: String): Date {
    var ex: ParseException? = null

    for (dateFormat in this) {
        try {
            // not inlined for debugging
            val result = dateFormat.parse(string)
            return result
        } catch (caught: ParseException) {
            ex = caught
        }
    }

    throw ex ?: IllegalArgumentException("Array is empty")
}