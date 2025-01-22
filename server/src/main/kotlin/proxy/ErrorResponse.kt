package io.github.darefox.hltbproxy.proxy

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.KotlinxSerialization.json

fun ErrorResponse(httpStatus: Status, message: String): Response {
    val json = buildJsonObject {
        put("code", httpStatus.code)
        put("error", httpStatus.description)
        put("message", message)
    }

    return Response(httpStatus)
        .with(Body.json().toLens() of json)
}