package io.github.darefox.hltbproxy.hltb

import io.github.darefox.hltbproxy.http4k.client
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.format.KotlinxSerialization.auto
import org.jsoup.Jsoup
import java.net.URLEncoder

object HLTB {
    fun queryGames(title: String, page: Int): HltbQueryResponse {
        val encoded = URLEncoder.encode(title, "utf-8")
        val url = "https://howlongtobeat.com/?q=$encoded"

        val queryObj = createQueryObj(title, page)
        val response = client(
            Request(POST, "https://howlongtobeat.com/api/search/4b4cbe570602c88660f7df8ea0cb6b6e").hltbJsonRequest(url, queryObj)
        )2

        return Body.auto<HltbQueryResponse>().toLens().invoke(response)
    }

    fun getOverviewInfoAboutGame(id: Long): HltbOverviewParser? {
        val url = "https://howlongtobeat.com/game/$id"
        val response = client(Request(GET, url).hltbDefaultHeaders(url, false))

        if (response.status.code == 404) {
            return null
        }

        val html = Jsoup.parse(response.bodyString())
        return HltbOverviewParser(html)
    }
}