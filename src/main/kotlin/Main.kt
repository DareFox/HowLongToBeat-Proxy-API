import cache.WeakExpiringLRUCache
import org.http4k.server.Jetty
import org.http4k.server.asServer
import proxy.serverRoutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

//import proxy.getServerRoutes

fun main(args: Array<String>) {
    serverRoutes.asServer(Jetty(1337)).start()
}