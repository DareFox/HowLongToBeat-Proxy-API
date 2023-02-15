package io.github.darefox.hltbproxy

import org.http4k.server.Jetty
import org.http4k.server.asServer
import io.github.darefox.hltbproxy.proxy.serverRoutes

//import proxy.getServerRoutes

fun main(args: Array<String>) {
    serverRoutes.asServer(Jetty(1337)).start()
}