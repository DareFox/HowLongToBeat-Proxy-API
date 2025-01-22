package io.github.darefox.hltbproxy.scraping

import kotlinx.serialization.Serializable

@Serializable
data class HltbSingleplayerTable(
    val mainStory: HltbSinglePlayerTime?,
    val extras: HltbSinglePlayerTime?,
    val completionist: HltbSinglePlayerTime?,
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

internal fun HltbTableParser.toSingleplayer(): HltbSingleplayerTable {
    require(title == "Single-Player")

    return HltbSingleplayerTable(
        mainStory = rows["Main Story"]?.let { getVariants(it) },
        extras = rows["Main + Extras"]?.let { getVariants(it) },
        completionist = rows["Completionist"]?.let { getVariants(it) },
        allPlaystyles = rows["All PlayStyles"]!!.let { getVariants(it) }
    )
}

private fun HltbTableParser.getVariants(column: Map<String, String>): HltbSinglePlayerTime {
    return HltbSinglePlayerTime(
        polled = column["Polled"]?.parseLongWithK(),
        averageSec = column["Average"]?.let { toSecondsOrNull(it) },
        medianSec = column["Median"]?.let { toSecondsOrNull(it) },
        rushedSec = column["Rushed"]?.let { toSecondsOrNull(it) },
        leisureSec = column["Leisure"]?.let { toSecondsOrNull(it) },
    )
}


