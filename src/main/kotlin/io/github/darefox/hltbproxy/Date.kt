package io.github.darefox.hltbproxy

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Parse until success or end of list
 *
 * @throws IllegalArgumentException if array is empty
 * @throws ParseException if all parsers failed
 */
fun List<SimpleDateFormat>.parse(string: String): Date {
    var ex: ParseException? = null
    lateinit var result: Date

    for (dateFormat in this) {
        try {
            return dateFormat.parse(string)
        } catch (caught: ParseException) {
            ex = caught
        }
    }

    throw ex ?: IllegalArgumentException("Array is empty")
}