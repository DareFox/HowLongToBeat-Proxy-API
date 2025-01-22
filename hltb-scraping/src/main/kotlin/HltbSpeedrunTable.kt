package io.github.darefox.hltbproxy.scraping

import kotlinx.serialization.Serializable

@Serializable
data class HltbSpeedrunTable(
    val anyPercentage: HltbSpeedrunTime?,
    val hundredPercentage: HltbSpeedrunTime?,
)

@Serializable
data class HltbSpeedrunTime(
    val polled: Long?,
    val averageSec: Long?,
    val medianSec: Long?,
    val leastSec: Long?,
    val mostSec: Long?
)

internal fun HltbTableParser.toSpeedrun(): HltbSpeedrunTable {
    require(title == "Speedruns")

    return HltbSpeedrunTable(
        anyPercentage = rows["Any%"]?.let { getVariants(it) },
        hundredPercentage = rows["100%"]?.let { getVariants(it) },
    )
}

private fun HltbTableParser.getVariants(column: Map<String, String>): HltbSpeedrunTime {
    return HltbSpeedrunTime(
        polled = column["Polled"]?.parseLongWithK(),
        averageSec = column["Average"]?.let { toSecondsOrNull(it) },
        medianSec = column["Median"]?.let { toSecondsOrNull(it) },
        leastSec = column["Fastest"]?.let { toSecondsOrNull(it) },
        mostSec = column["Slowest"]?.let { toSecondsOrNull(it) },
    )
}
