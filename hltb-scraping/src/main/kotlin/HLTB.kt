package io.github.darefox.hltbproxy.scraping

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import org.http4k.client.ApacheClient
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
    private val httpClient = ApacheClient()
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
            val request = Request(POST, "https://howlongtobeat.com/api/seek/${getSearchKey()}")
                .hltbJsonRequest(url, queryObj)
            val call = httpClient(request)

            val isResponseInHtml = call.bodyString().startsWith("<!DOCTYPE html>")


            if (call.status.successful) {
                if (isResponseInHtml) {
                    if (keyIsNotUpdated) {
                        log.info("Search key is no longer valid, calling update")
                        updateSearchKey()
                        keyIsNotUpdated = false
                        continue
                    }
                    error("Search key was updated, but server returned html. Maybe search API was changed")
                } else {
                    response = call
                    break
                }
            } else {
                val normalizedHtml = Jsoup.parse(call.bodyString()).text()
                error("Server returned not successful code: ${call.status.code}\n\n\nBody: $normalizedHtml")
            }
        }

        return Body.auto<HltbQueryResponse>().toLens().invoke(response)
    }

    fun getOverviewInfoAboutGame(id: Long): HltbOverviewParser? {
        val url = "https://howlongtobeat.com/game/$id"
        val response = httpClient(Request(GET, url).hltbDefaultHeaders(url, false))

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
                    val request = Request(GET, url).hltbDefaultHeaders(url, false)
                    val response = httpClient(request)

                    val bodyString = response.bodyString()
                    val html = Jsoup.parse(bodyString)

                    if (!response.status.successful) {
                        log.error { "Response is not ok, body: ${response.bodyString()}" }
                        error("Can't get search key, HLTB server returned ${response.status.code}")
                    }


                    html.setBaseUri(url)

                    val scripts = html.select("script")

                    val codeOfScripts = getCodeOfInlineScripts(scripts) + getCodeOfExternalScripts(scripts)
                    val key = findKeyInScripts(codeOfScripts)

                    searchKey = key
                    log.info("Key is $searchKey")
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

        return externalScripts.asSequence().mapNotNull { scriptUrl ->
            log.info("Downloading script: $scriptUrl")
            val response = httpClient(Request(GET, scriptUrl).hltbDefaultHeaders(scriptUrl, false))
            if (response.status != Status.OK) {
                log.error("On downloading script ($scriptUrl), server returned ${response.status}")
                return@mapNotNull null
            }
            response.bodyString()
        }
    }

    private fun findKeyInScripts(scripts: Sequence<String>): String {
        val fetchLineRegex = "(?<=api/seek/).*?,".toRegex()
        val concatValuesRegex = "(?<=concat\\(\").*?(?=\")".toRegex()
        for (script in scripts) {
            val fetchLine = fetchLineRegex.find(script) ?: continue
            log.info("found fetch line: ${fetchLine.value}")
            val concatValues = concatValuesRegex.findAll(fetchLine.value).toList()
            if (concatValues.isEmpty()) {
                log.info("but concat values are empty")
                continue
            }

            return concatValues.joinToString("") { it.value }
        }

        error("Can't find search key")
    }
}
