package io.github.darefox.hltbproxy.hltb

import kotlinx.serialization.Serializable

@Serializable
data class HltbSingleplayerTable(
    val mainStory: HltbSinglePlayerTime,
    val extras: HltbSinglePlayerTime,
    val completionist: HltbSinglePlayerTime,
    val allPlaystyles: HltbSinglePlayerTime
)

@Serializable
data class HltbSinglePlayerTime(
    val polled: Long?,
    val averageSec: Long?,
    val medianSec: Long?,
    val rushedSec: Long?,
    val leisureSec: Long?
)

fun HltbTableParser.toSingleplayer(): HltbSingleplayerTable {
    require(title == "Single-Player")

    return HltbSingleplayerTable(
        mainStory = getVariants(rows["Main Story"]!!),
        extras = getVariants(rows["Main + Extras"]!!),
        completionist = getVariants(rows["Completionist"]!!),
        allPlaystyles = getVariants(rows["All PlayStyles"]!!)
    )
}

private fun HltbTableParser.getVariants(column: Map<String, String>): HltbSinglePlayerTime {
    return HltbSinglePlayerTime(
        polled = column["Polled"]?.toLongOrNull(),
        averageSec = column["Average"]?.let { toSecondsOrNull(it) },
        medianSec = column["Median"]?.let { toSecondsOrNull(it) },
        rushedSec = column["Rushed"]?.let { toSecondsOrNull(it) },
        leisureSec = column["Leisure"]?.let { toSecondsOrNull(it) },
    )
}


