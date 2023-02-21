package io.github.darefox.hltbproxy.hltb

data class HltbSingleplayerTable(
    val mainStory: HltbSinglePlayerTime,
    val extras: HltbSinglePlayerTime,
    val completionist: HltbSinglePlayerTime,
    val allPlaystyles: HltbSinglePlayerTime
)

data class HltbSinglePlayerTime(
    val averageMin: Int?,
    val medianMin: Int?,
    val rushedMin: Int?,
    val leisureMin: Int?
)

fun HltbTableParser.toSingleplayer(): HltbSingleplayerTable {
    require(title == "Single-Player")

    return HltbSingleplayerTable(
        mainStory = getVariants(rows["Main Story"]!!),
        extras = getVariants(rows["Main + Extras"]!!),
        completionist = getVariants(rows["Completionist"]!!),
        allPlaystyles =  getVariants(rows["All PlayStyles"]!!)
    )
}

private fun HltbTableParser.getVariants(column: Map<String,String>): HltbSinglePlayerTime {
    return HltbSinglePlayerTime(
        averageMin = column["Average"]?.let { toMinutesOrNull(it) },
        medianMin = column["Median"]?.let { toMinutesOrNull(it) },
        rushedMin = column["Rushed"]?.let { toMinutesOrNull(it) },
        leisureMin = column["Leisure"]?.let { toMinutesOrNull(it) },
    )
}


