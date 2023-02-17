package io.github.darefox.hltbproxy.hltb

import io.github.darefox.hltbproxy.http4k.client
import io.github.darefox.hltbproxy.proxy.QueryGamesResponse
import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.KotlinxSerialization.auto
import java.net.URLEncoder

object HLTB {
    fun queryGames(title: String, page: Int): HltbQueryResponse {
        val encoded = URLEncoder.encode(title, "utf-8")
        val url = "https://howlongtobeat.com/?q=$encoded"

        val queryObj = createQueryObj(title, page)
        val response =  client(
            Request(POST, "https://howlongtobeat.com/api/search").hltbJsonRequest(url, queryObj)
        )

        return Body.auto<HltbQueryResponse>().toLens().invoke(response)
    }

