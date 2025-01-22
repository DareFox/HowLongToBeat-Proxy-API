import io.github.darefox.hltbproxy.HLTBProxyServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import java.net.URL

@Tag("LocalSystemTest")
class LocalServerTest: ServerSystemTest(URL("http://localhost:8080")) {
    private lateinit var serverInstance: HLTBProxyServer

    @BeforeEach
    fun `Create server instance`() {
        serverInstance = HLTBProxyServer().apply { start() }
    }

    @AfterEach
    fun `Close server`() {
        serverInstance.stop()
        serverInstance.close()
    }
}
