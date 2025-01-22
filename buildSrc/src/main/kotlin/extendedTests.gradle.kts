import java.net.URL

interface RemoteTest {
    val enabled: Property<Boolean>
    val url: Property<URL>
}

val remoteTestExt = extensions.create<RemoteTest>("remoteTest")
remoteTestExt.enabled = false

afterEvaluate {
    val tests = tasks.withType<Test>()
    for (test in tests) {
        val location = test.reports.junitXml.outputLocation
        tasks.register<Delete>("${test.name}-cleanResults") {
            setDelete(location)
        }
    }
}

tasks.register("remoteSystemTest") {
    remoteTestExt.enabled = true
    dependsOn("systemTest")
}
