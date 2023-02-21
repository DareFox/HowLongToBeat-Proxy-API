package io.github.darefox.hltbproxy.proxy

import io.github.darefox.hltbproxy.cache.getOrGenerateBlockingJson
import io.github.darefox.hltbproxy.hltb.*
import kotlinx.serialization.Serializable
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.json
import org.http4k.lens.Query

val getOverviewInfo: HttpHandler = { req ->
    val idLens = Query.required("id")

    val id = idLens(req).toLong()
    val response = Response(Status.OK)

    val key = "overviewInfo;$id"
    val jsonBody = cache.getOrGenerateBlockingJson(mutexMap, key) {
        HLTB.getOverviewInfoAboutGame(id).toProxy()
    }

    response.with(Body.json().toLens() of jsonBody)
}

@Serializable
data class OverviewInfo(
    val singleplayerTime: HltbSingleplayerTable?,
    val multiplayerTime: HltbMultiplayerTable?,
    val speedrunTime: HltbSpeedrunTable?,
    val platformsTime: HltbPlatformTable?,
    val platforms: List<String>,
    val genres: List<String>,
    val developers: List<String>,
    val publishers: List<String>,
    val northAmericaRelease: Long?,
    val europeRelease: Long?,
    val japanRelease: Long?
)

fun HltbOverviewParser.toProxy(): OverviewInfo {
    return OverviewInfo(
        singleplayerTime = singleplayerTimeTable,
        multiplayerTime = multiplayerTimeTable,
        speedrunTime = speedrunTimeTable,
        platformsTime = platformTimeTable,
        platforms = platforms,
        genres = genres,
        developers = developers,
        publishers = publishers,
        northAmericaRelease = northAmericaRelease,
        europeRelease = europeRelease,
        japanRelease = japanRelease
    )
}