import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.net.URL

@Execution(ExecutionMode.CONCURRENT)
@Tag("RemoteSystemTest")
class RemoteServerTest: ServerSystemTest(URL(System.getProperty("systemTest.server.remoteUrl")))