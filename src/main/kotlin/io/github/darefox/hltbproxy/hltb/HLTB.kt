package io.github.darefox.hltbproxy.hltb

import io.github.darefox.hltbproxy.http4k.client
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.auto
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.net.URLEncoder

object HLTB {
    private val log = KotlinLogging.logger { }
    lateinit var searchKey: String
    private val keyMutex = Mutex()
    private val updateMutex = Mutex()
    private val initMutex = Mutex()

    suspend fun queryGames(title: String, page: Int): HltbQueryResponse {
        val encoded = URLEncoder.encode(title, "utf-8")
        val url = "https://howlongtobeat.com/?q=$encoded"

        val queryObj = createQueryObj(title, page)

        var keyIsNotUpdated = true
        lateinit var response: Response
        while (true) {
            val call = client(
                Request(POST, "https://howlongtobeat.com/api/search/${getSearchKey()}")
                    .hltbJsonRequest(url, queryObj)
            )

            if (call.bodyString().startsWith("<!DOCTYPE html>") && keyIsNotUpdated) {
                log.info("Search key is no longer valid, calling update")
                updateSearchKey()
                keyIsNotUpdated = false
            } else {
                response = call
                break
            }
        }

        if (response.bodyString().startsWith("<!DOCTYPE html>")) {
            updateSearchKey()
        }

        return Body.auto<HltbQueryResponse>().toLens().invoke(response)
    }

    suspend fun getOverviewInfoAboutGame(id: Long): HltbOverviewParser? {
        val url = "https://howlongtobeat.com/game/$id"
        val response = client(Request(GET, url).hltbDefaultHeaders(url, false))

        if (response.status.code == 404) {
            return null
        }

        val html = Jsoup.parse(response.bodyString())
        return HltbOverviewParser(html)
    }

    private suspend fun getSearchKey(): String {
        if (!this::searchKey.isInitialized) {
            initMutex.withLock {
                if (!this::searchKey.isInitialized) {
                    return updateSearchKey()
                } else {
                    return keyMutex.withLock { searchKey }
                }
            }
        } else {
            keyMutex.withLock { return searchKey }
        }
    }

    private suspend fun updateSearchKey(): String {
        // Prevent creating multiple calls to updateSearchKey
        // First call updates key, remaining calls wait for new key
        return if (updateMutex.isLocked) getSearchKey() else {
            return updateMutex.withLock {
                log.info("Updating key...")
                keyMutex.withLock { // keyMutex needed for API calls to wait new key
                    val url = "https://howlongtobeat.com"
                    val response = client(Request(GET, url).hltbDefaultHeaders(url, false))

                    if (response.status != Status.OK) {
                        error("Response is not 199 from HLTB server, can't get search key")
                    }

                    val html = Jsoup.parse(response.bodyString())
                    html.setBaseUri(url)

                    val scripts = html.select("script")

                    val codeOfScripts = getCodeOfInlineScripts(scripts) + getCodeOfExternalScripts(scripts)
                    val key = findKeyInScripts(codeOfScripts)

                    searchKey = key
                    key
                }
            }
        }
    }

    private fun getCodeOfInlineScripts(scripts: Elements): Sequence<String> {
        val inlinedScripts = scripts.filter { element ->
            val isJS = element.attr("type") == "text/javascript"
            val isExternal = element.attr("src").isNotEmpty()
            isJS && !isExternal
        }
        return inlinedScripts.asSequence().map { it.text() }
    }

    private fun getCodeOfExternalScripts(scripts: Elements): Sequence<String> {
        val externalScripts = scripts.filter { element ->
            val isExternal = element.attr("src").isNotEmpty()
            isExternal
        }.map {
            it.attr("abs:src")
        }

        return externalScripts.asSequence().map { scriptUrl ->
            log.info("Downloading script: $scriptUrl")
            val response = client(Request(GET, scriptUrl).hltbDefaultHeaders(scriptUrl, false))
            if (response.status != Status.OK) {
                error("On downloading script ($scriptUrl), server returned ${response.status}")
            }
            response.bodyString()
        }
    }

    private fun findKeyInScripts(codes: Sequence<String>): String {
        val regex = "(?<=api/search/\"\\.concat\\(\")[a-zA-Z0-9]+".toRegex()
        for (code in codes) {
            val result = regex.find(code)?.value ?: continue
            return result
        }

        error("Can't find search key")
    }
}
