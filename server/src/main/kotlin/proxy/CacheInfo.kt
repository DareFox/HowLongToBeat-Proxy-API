package io.github.darefox.hltbproxy.proxy

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto

val cacheInfo: HttpHandler = {
    val json = buildJsonObject {
        put("lifetime", JsonPrimitive(cache.lifetime.toString()))
        put("size", JsonPrimitive(cache.size))
        put("maxSize", JsonPrimitive(cache.maxSize))
    }

   Response(Status.OK).with(
       Body.auto<JsonObject>().toLens() of json
   )
}