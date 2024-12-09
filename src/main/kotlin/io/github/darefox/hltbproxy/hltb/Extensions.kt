package io.github.darefox.hltbproxy.hltb

import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.format.KotlinxSerialization.auto

fun Request.hltbDefaultHeaders(url: String, bodyIsJson: Boolean = true): Request {
    val headers = mutableListOf(
        "Host" to "howlongtobeat.com",
        "Origin" to "https://howlongtobeat.com",
        "Referer" to "$url",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:133.0) Gecko/20100101 Firefox/133.0",
        "Accept" to "*/*"
    ).also {
        if (bodyIsJson) it += "Content-Type" to "application/json"
    }

    return this.headers(headers)
}

inline fun <reified T : Any> Request.hltbJsonRequest(url: String, body: T): Request {
    val lens = Body.auto<T>().toLens()

    return this.headers(headers).hltbDefaultHeaders(url, true).with(lens of body)
}