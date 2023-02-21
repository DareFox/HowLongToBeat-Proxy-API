package io.github.darefox.hltbproxy.hltb

data class HltbMultiplayerTable(
    val coop: HltbMultiPlayerTime,
    val competitive: HltbMultiPlayerTime,
)

data class HltbMultiPlayerTime(
    val averageMin: Int?,
    val medianMin: Int?,
    val leastMin: Int?,
    val mostMin: Int?
)

fun HltbTableParser.toMultiPlayer(): HltbMultiplayerTable {
    require(title == "Multi-Player")

    return HltbMultiplayerTable(
        coop = getVariants(rows["Co-Op"]!!),
        competitive = getVariants(rows["Competitive"]!!),
    )
}

private fun HltbTableParser.getVariants(column: Map<String,String>): HltbMultiPlayerTime {
    return HltbMultiPlayerTime(
        averageMin = column["Average"]?.let { toMinutesOrNull(it) },
        medianMin = column["Median"]?.let { toMinutesOrNull(it) },
        leastMin = column["Least"]?.let { toMinutesOrNull(it) },
        mostMin = column["Most"]?.let { toMinutesOrNull(it) },
    )
}
