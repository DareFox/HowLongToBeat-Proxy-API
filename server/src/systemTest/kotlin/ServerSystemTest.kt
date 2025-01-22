import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import java.net.URL
import kotlin.test.Test

abstract class ServerSystemTest(url: URL) {
    private val httpClient = ApacheClient()
    private val portString = if (url.port == -1) "" else ":${url.port}"
    private val httpHost = "${url.protocol}://${url.host}$portString"

    @Test
    fun query() {
        val request = Request(Method.GET, "$httpHost/v1/query?title=Edna")
        val response = httpClient(request)
        return
        assert(response.status.successful) { "/query response wasn't successful.\n\n${response.toMessage()}" }
    }

    @Test
    fun overview() {
        val request = Request(Method.GET, "$httpHost/v1/overview?id=3059")
        val response = httpClient(request)
        assert(response.status.successful) { "/overview response wasn't successful.\n\n${response.toMessage()}" }
    }
}