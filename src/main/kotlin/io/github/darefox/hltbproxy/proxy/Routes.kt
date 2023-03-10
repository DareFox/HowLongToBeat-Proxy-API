package io.github.darefox.hltbproxy.proxy

import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes

val serverRoutes = routes(
    "/" bind GET to {
        Response(OK).body(
            "Sleepin' on a subway.\n" +
                    "City up above me. Dreamin' up the words to this song.\n" +
                    "Bettin' on a someday. Wakin' up at someplace.\n" +
                    "Believe me, baby, I know\n\n" +
                    "https://github.com/DareFox/HowLongToBeat-Proxy-API"
        )
    },
    "/v1/query" bind GET to queryGames,
    "/v1/overview" bind GET to getOverviewInfo,
    "/v1/cache" bind GET to cacheInfo
).withFilter(corsAll)

