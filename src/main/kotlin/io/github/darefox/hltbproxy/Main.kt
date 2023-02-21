package io.github.darefox.hltbproxy

import io.github.darefox.hltbproxy.proxy.serverRoutes
import org.http4k.server.Jetty
import org.http4k.server.asServer


fun main(args: Array<String>) {
    serverRoutes.asServer(Jetty(1337)).start()
}