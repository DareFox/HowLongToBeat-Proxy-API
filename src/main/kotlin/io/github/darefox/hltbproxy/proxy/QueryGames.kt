package io.github.darefox.hltbproxy.proxy

import io.github.darefox.hltbproxy.cache.getOrGenerateBlocking
import io.github.darefox.hltbproxy.cache.getOrGenerateBlockingJson
import io.github.darefox.hltbproxy.hltb.HLTB
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.format.KotlinxSerialization.json
import org.http4k.lens.Query

val queryGames: HttpHandler = { req ->
    val nameLens = Query.required("title")
    val pageLens = Query.defaulted("page", "1")

    val name = nameLens(req)
    val page = pageLens(req).toInt()

    val key = "queryGames;$name;$page"

    cache.getOrGenerateBlocking(mutexMap, key) {
        val body = HLTB.queryGames(name, page).data.map {
            it.toProxyObj()
        }

        Response(Status.OK)
            .with(Body.auto<List<QueryGamesResponse>>().toLens() of body)
    }
}