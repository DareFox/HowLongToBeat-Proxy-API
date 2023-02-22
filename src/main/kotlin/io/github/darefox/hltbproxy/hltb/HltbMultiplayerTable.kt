package io.github.darefox.hltbproxy.hltb

import kotlinx.serialization.Serializable

@Serializable
data class HltbMultiplayerTable(
    val coop: HltbMultiPlayerTime?,
    val competitive: HltbMultiPlayerTime?,
)

@Serializable
data class HltbMultiPlayerTime(
    val polled: Long?,
    val averageSec: Long?,
    val medianSec: Long?,
    val leastSec: Long?,
    val mostSec: Long?
)

fun HltbTableParser.toMultiPlayer(): HltbMultiplayerTable {
    require(title == "Multi-Player")

    return HltbMultiplayerTable(
        coop = getVariants(rows["Co-Op"]!!),
        competitive = getVariants(rows["Competitive"]!!),
    )
}

private fun HltbTableParser.getVariants(column: Map<String, String>): HltbMultiPlayerTime {
    return HltbMultiPlayerTime(
        polled = column["Polled"]?.toLongOrNull(),
        averageSec = column["Average"]?.let { toSecondsOrNull(it) },
        medianSec = column["Median"]?.let { toSecondsOrNull(it) },
        leastSec = column["Least"]?.let { toSecondsOrNull(it) },
        mostSec = column["Most"]?.let { toSecondsOrNull(it) },
    )
}
