package io.github.darefox.hltbproxy

import io.github.darefox.hltbproxy.proxy.serverRoutes
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main(args: Array<String>) {
    val port = System.getenv("PORT")?.toIntOrNull() ?: HLTBProxyServer.DEFAULT_PORT
    HLTBProxyServer(port).start()
}

class HLTBProxyServer(val port: Int = DEFAULT_PORT) {
    private val proxyContext = Dispatchers.IO + CoroutineName("HLTBProxyServer Scope")
    private val server = serverRoutes.asServer(Jetty(port))

    fun start() = server.start()
    fun stop() = server.stop()
    fun close() = server.close()

    companion object {
        val DEFAULT_PORT = 8080
    }
}