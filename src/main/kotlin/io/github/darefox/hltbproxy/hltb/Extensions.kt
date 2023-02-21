package io.github.darefox.hltbproxy.hltb

import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.format.KotlinxSerialization.auto

fun Request.hltbDefaultHeaders(url: String, bodyIsJson: Boolean = true): Request {
    val headers = mutableListOf(
        "Authority" to "howlongtobeat.com",
        "Origin" to "https://howlongtobeat.com",
        "Referer" to "$url",
        "User-Agent" to "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36"
    ).also {
        if (bodyIsJson) it += "Content-Type" to "application/json"
    }

    return this.headers(headers)
}

inline fun <reified T : Any> Request.hltbJsonRequest(url: String, body: T): Request {
    val lens = Body.auto<T>().toLens()

    return this.headers(headers).hltbDefaultHeaders(url, true).with(lens of body)
}