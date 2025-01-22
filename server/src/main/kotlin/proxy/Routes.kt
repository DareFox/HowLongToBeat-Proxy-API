package io.github.darefox.hltbproxy.proxy

import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes

val serverRoutes = routes(
    "/" bind GET to {
        Response(Status.TEMPORARY_REDIRECT)
            .header("Location", "https://github.com/DareFox/HowLongToBeat-Proxy-API")
    },
    "/v1/query" bind GET to queryGames,
    "/v1/overview" bind GET to getOverviewInfo,
    "/v1/cache" bind GET to cacheInfo
).withFilter(corsAll)

