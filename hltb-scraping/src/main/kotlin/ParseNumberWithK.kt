package io.github.darefox.hltbproxy.scraping

import java.lang.NumberFormatException

private fun parseStringWithKtoLong(string: String): Long {
    val withoutKparse = "^\\d*\$".toRegex().find(string)?.value?.toLongOrNull()

    if (withoutKparse != null) {
        return withoutKparse
    }

    val exponent = "\\d+(?=(.|)\\d+(K|k))"
        .toRegex()
        .find(string)
        ?.value
        ?.toLongOrNull()
        ?.times(1000)

    val mantissa = "(?<=\\d(\\.|))\\d+(?=(K|k))"
        .toRegex()
        .find(string)
        ?.value
        ?.toLongOrNull()
        ?.times(100)

    return (mantissa ?: 0) + (exponent ?: 0)
}

/**
 * Parse numbers with K at the end
 *
 * Example
 * ```
 * "1.3k".parseLongWithK() == 1300
 * "13".parseLongWithK() == 13
 * "k13".parseLongWithK() == null
 * ```
 */
internal fun String.parseLongWithK(): Long {
    return parseStringWithKtoLong(this)
}

/**
 * Parse numbers with K at the end
 *
 * Example
 * ```
 * "1.3k".parseIntWithK() == 1300
 * "13".parseIntWithK() == 13
 * "k13".parseIntWithK() == null
 * ```
 */
internal fun String.parseIntWithK(): Int? {
    val result = parseStringWithKtoLong(this)

    if (result !in Int.MIN_VALUE..Int.MAX_VALUE) {
        throw NumberFormatException("$result is not in range of Int")
    }

    return result.toInt()
}