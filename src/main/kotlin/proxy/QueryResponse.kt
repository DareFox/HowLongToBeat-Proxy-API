package proxy

import hltb.HltbGameData
import hltb.HltbQueryResponse
import kotlinx.serialization.Serializable

@Serializable
data class QueryGamesResponse(
    val gameName: String,
    val type: String,
    val gameId: Long,
    val gameImage: String,
    val dev: String,
    val platforms: List<String>,
    val releaseYear: Int,
    val beatTime: QueryGamesBeatTime,
    val counters: QueryGamesCounters,
    val steamId: Long?
)

@Serializable
data class QueryGamesBeatTime(
    val main: QueryGamesBeatTimeEntry,
    val extra: QueryGamesBeatTimeEntry,
    val completionist: QueryGamesBeatTimeEntry,
    val all: QueryGamesBeatTimeEntry,
)

@Serializable
data class QueryGamesBeatTimeEntry(
    val avgSeconds: Long,
    val polledCount: Long,
)

@Serializable
data class QueryGamesCounters(
    val beated: Long,
    val speedruns: Long,
    val backlogs: Long,
    val reviews: Long,
    val retired: Long
)

fun HltbGameData.toProxyObj(): QueryGamesResponse {
    return QueryGamesResponse(
        gameName = this.gameName,
        type = this.gameType,
        gameId = this.gameId,
        gameImage = this.gameImage,
        dev = this.profileDev,
        platforms = this.profilePlatform.trim().split(" "),
        releaseYear = this.releaseWorld.toInt(),
        beatTime = QueryGamesBeatTime(
            main = QueryGamesBeatTimeEntry(compMain, compMainCount),
            extra = QueryGamesBeatTimeEntry(compPlus, compPlusCount),
            completionist = QueryGamesBeatTimeEntry(comp100, comp100Count),
            all = QueryGamesBeatTimeEntry(compAll, compAllCount),
        ),
        counters = QueryGamesCounters(
            beated = this.countComp ,
            speedruns = countSpeedrun,
            backlogs = countBacklog,
            retired = countRetired,
            reviews = countReview,
        ),
        steamId = if (profileSteam == 0L) null else profileSteam
    )
}