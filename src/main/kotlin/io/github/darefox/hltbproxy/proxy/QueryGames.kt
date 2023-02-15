package io.github.darefox.hltbproxy.proxy

import io.github.darefox.hltbproxy.cache.getOrGenerateBlockingJson
import io.github.darefox.hltbproxy.hltb.HLTB
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.json
import org.http4k.lens.Query

val queryGames: HttpHandler = { req ->
    val nameLens = Query.required("title")
    val pageLens = Query.defaulted("page", "1")

    val name = nameLens(req)
    val page = pageLens(req).toInt()
    val response = Response(Status.OK)

    val key = "queryGames;$name;$page"
    val jsonBody = cache.getOrGenerateBlockingJson(mutexMap, key) {
        HLTB.queryGames(name, page).data.map {
            it.toProxyObj()
        }
    }

    response.with(Body.json().toLens() of jsonBody)
}