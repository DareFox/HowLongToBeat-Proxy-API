package hltb

import http4k.client
import kotlinx.serialization.Serializable
import org.http4k.core.Body
import org.http4k.core.Method.*
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.KotlinxSerialization.auto
import proxy.QueryGamesResponse
import java.net.URLEncoder

object HLTB {
    fun queryGames(title: String, page: Int): HltbQueryResponse {
        val response = requestSearch(title, page)
        val lens = Body.auto<HltbQueryResponse>().toLens()

        return lens(response)
    }

    private fun requestSearch(title: String, page: Int): Response {
        val encoded = URLEncoder.encode(title, "utf-8")
        val requestObj = createRequestObj(title, page)
        val requestLens = Body.auto<HltbQueryRequest>().toLens()

        val requestWithJson = requestLens(requestObj, Request(POST, "https://howlongtobeat.com/api/search").headers(
            listOf(
                "Authority" to "howlongtobeat.com",
                "Content-Type" to "application/json",
                "Origin" to "https://howlongtobeat.com",
                "Referer" to "https://howlongtobeat.com/?q=$encoded",
                "User-Agent" to "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36"
            )
        ))

        return client(requestWithJson)
    }
}

@Serializable
private data class SearchResponse(
    val data: List<QueryGamesResponse>
)