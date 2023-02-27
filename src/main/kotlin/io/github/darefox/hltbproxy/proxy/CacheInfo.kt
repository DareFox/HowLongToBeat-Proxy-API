package io.github.darefox.hltbproxy.proxy

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status

val cacheInfo: HttpHandler = {
   Response(Status.OK).body(cache.size.toString())
}