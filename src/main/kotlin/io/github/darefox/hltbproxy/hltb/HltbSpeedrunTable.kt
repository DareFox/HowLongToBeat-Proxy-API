package io.github.darefox.hltbproxy.hltb

import kotlinx.serialization.Serializable

@Serializable
data class HltbSpeedrunTable(
    val anyPercentage: HltbSpeedrunTime,
    val hundredPercentage: HltbSpeedrunTime,
)

@Serializable
data class HltbSpeedrunTime(
    val averageSec: Long?,
    val medianSec: Long?,
    val leastSec: Long?,
    val mostSec: Long?
)

fun HltbTableParser.toSpeedrun(): HltbSpeedrunTable {
    require(title == "Speedruns")

    return HltbSpeedrunTable(
        anyPercentage = getVariants(rows["Any%"]!!),
        hundredPercentage = getVariants(rows["100%"]!!),
    )
}

private fun HltbTableParser.getVariants(column: Map<String,String>): HltbSpeedrunTime {
    return HltbSpeedrunTime(
        averageSec = column["Average"]?.let { toSecondsOrNull(it) },
        medianSec = column["Median"]?.let { toSecondsOrNull(it) },
        leastSec = column["Fastest"]?.let { toSecondsOrNull(it) },
        mostSec = column["Slowest"]?.let { toSecondsOrNull(it) },
    )
}
