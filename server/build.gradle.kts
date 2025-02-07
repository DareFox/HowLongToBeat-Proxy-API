import GithubIssue_gradle.*
import java.net.URL

plugins {
    application
    id("convention")
    id("extendedTests")
    id("githubIssue")
    `jvm-test-suite`
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.shadowJar)
}

dependencies {
    implementation(project(":cache"))
    implementation(project(":hltb-scraping"))
    implementation(project(":utils"))
    implementation(libs.bundles.http4kServer)
    implementation(libs.bundles.logging)
    implementation(libs.bundles.kotlinx)
}

application {
    mainClass = "io.github.darefox.hltbproxy.MainKt"
}

tasks.jar {
    manifest.attributes["Main-Class"] = "io.github.darefox.hltbproxy.MainKt"
}

remoteTest {
    url = URL("https://hltb-proxy.fly.dev")
}

val markdownReportTask = tasks.register<JUnitToMarkdown>("createMarkdownReport") {
    junitXmlDir.set(layout.buildDirectory.dir("test-results/systemTest"))
    markdownOutput.set(layout.buildDirectory.file("markdown/report.md"))
    filterPattern.set { it.extension == "xml" }
}

@Suppress("UnstableApiUsage")
testing.suites.register<JvmTestSuite>("systemTest") {
    dependencies {
        implementation(project())
        useKotlinTest("2.1.0")
        implementation.bundle(libs.bundles.http4kClient)
        implementation(libs.kotlinXcoroutines)
    }
    targets.all {
        testTask.configure {
            doFirst {
                if (remoteTest.enabled.get()) {
                    systemProperty("systemTest.server.remoteUrl", remoteTest.url.get().toString())
                    exclude("Local*")
                }
                else exclude("Remote*")
            }
            shouldRunAfter("test")
            finalizedBy(markdownReportTask)
            outputs.upToDateWhen { false } // DO NOT CACHE TESTS
        }
    }
}

val systemTestTask = tasks.named<Test>("systemTest")
tasks.named("check") { dependsOn(systemTestTask) }

val GITHUB_TOKEN = project.objects.property<String>()
GITHUB_TOKEN.set(System.getenv("GITHUB_TOKEN") ?: System.getProperty("GITHUB_TOKEN"))

fun CreateGithubIssue.setup() {
    gradle.taskGraph.whenReady {
        if (allTasks.all { it.name != "systemTest" }) {
            throw GradleException("CreateGithubIssue task should be together executed with systemTest")
        }
        if (gradle.startParameter.isContinueOnFailure.not()) {
            throw GradleException("CreateGithubIssue task was called, but without --continue. Without it this task will be skipped anyway")
        }
    }
    githubToken = GITHUB_TOKEN
    markdownFile = markdownReportTask.get().markdownOutput
    issueLabels = listOf("bug", "auto-report")
}

fun CloseGithubIssue.setup() {
    gradle.taskGraph.whenReady {
        if (allTasks.all { it.name != "systemTest"} ) {
            throw GradleException("CloseGithubIssue task should be together executed with systemTest")
        }
    }
    githubToken = GITHUB_TOKEN
}

val remoteIssueId = "systemTest-remote-server"
tasks.register<CreateGithubIssue>("createIssueOnRemoteFailure") {
    setup()
    onlyIf { systemTestTask.get().state.failure != null && remoteTest.enabled.get() }
    remoteTest.enabled = true
    title = "Server returns errors"
    uniqueId = remoteIssueId
    mustRunAfter("createMarkdownReport")
}
tasks.register<CloseGithubIssue>("closeIssueOnRemoteSuccess") {
    setup()
    onlyIf { systemTestTask.get().state.failure == null && remoteTest.enabled.get() }
    remoteTest.enabled = true
    uniqueId = remoteIssueId
    reason = "Status has changed. Testing is now successful."
    mustRunAfter("createMarkdownReport")
}

val localIssueId = "systemTest-local-server"
tasks.register<CreateGithubIssue>("createIssueOnLocalFailure") {
    setup()
    onlyIf { systemTestTask.get().state.failure != null && remoteTest.enabled.get().not() }
    title = "Local server returns errors"
    uniqueId = localIssueId
    mustRunAfter("createMarkdownReport")
}
tasks.register<CloseGithubIssue>("closeIssueOnLocalSuccess") {
    setup()
    onlyIf { systemTestTask.get().state.failure == null && remoteTest.enabled.get().not() }
    uniqueId = localIssueId
    reason = "Status has changed. Testing is now successful."
    mustRunAfter("createMarkdownReport")
}
