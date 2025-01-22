import junitParse.JunitParser
import org.gradle.api.DefaultTask
import org.kohsuke.github.*

abstract class JUnitToMarkdown : DefaultTask() {
    @get:InputDirectory
    abstract val junitXmlDir: DirectoryProperty

    @get:Input
    abstract val filterPattern: Property<(File) -> Boolean>

    @get:OutputFile
    abstract val markdownOutput: RegularFileProperty

    @TaskAction
    fun run() {
        val files = junitXmlDir.asFileTree.files
        val filteredFiles = files.filterNotNull()
            .filter { it.isFile }
            .filter { filterPattern.get().invoke(it) }
        val parsed = filteredFiles.map { file ->
            JunitParser.parse(file.readText())
        }
        val markdown = MarkdownConverter.convert(parsed)
        val outputFile = markdownOutput.get().asFile
        if (outputFile.exists()) {
            require(outputFile.delete()) { "Can't delete previous result" }
        }

        outputFile.parentFile.mkdirs()
        require(outputFile.createNewFile()) { "Can't create output file" }

        outputFile.writeText(markdown.toList().joinToString("\n"))
    }
}

private object Util {
    fun gradleID(issueId: String?) = "[gradleID: $issueId]"

    fun getIssueByTitleId(repo: GHRepository, id: String, authorId: Long): GHIssue? {
        val allIssues = repo.getIssues(GHIssueState.ALL)
        val previousIssuesByID = allIssues.filter { issue ->
            val idTitleMatched = issue.title.endsWith(gradleID(id))
            val userMatched = issue.user.id == authorId
            idTitleMatched && userMatched
        }
        val currentIssue = previousIssuesByID.firstOrNull { issue ->
            issue.state == GHIssueState.OPEN
        }

        val latestClosedIssue = previousIssuesByID
            .filter { it.state == GHIssueState.CLOSED }
            .sortedByDescending { it.createdAt }
            .firstOrNull()

        return currentIssue ?: latestClosedIssue
    }

    fun buildGithub(token: String): AuthGithubStuff {
        val github = GitHubBuilder().withOAuthToken(token).build()
        val myself = github.myself
        println("Logged in as ${myself.name} at ${myself.htmlUrl}")
        return AuthGithubStuff(github, myself)
    }

}
private data class AuthGithubStuff(
    val github: GitHub,
    val myself: GHMyself
)

abstract class CreateGithubIssue : DefaultTask() {
    init {
        group = "verification"
    }

    @get:Input
    abstract val githubToken: Property<String>

    @get:Input
    abstract val title: Property<String>

    @get:Input
    abstract val uniqueId: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "issue-dry-run", description = "Controls if issue will be created or not")
    abstract val dryRun: Property<Boolean>

    @get:Input
    abstract val issueLabels: ListProperty<String>

    @get:InputFile
    abstract val markdownFile: RegularFileProperty

    @TaskAction
    fun action() {
        val issueId = uniqueId.get()
        val trimmedTitle = title.get().trim()
        val (github, myself) = Util.buildGithub(githubToken.get())
        val repo = github.getRepository("DareFox/HowLongToBeat-Proxy-API")

        val latestIssue = Util.getIssueByTitleId(repo, issueId, myself.id)
        val gradleID = Util.gradleID(issueId)
        if (latestIssue?.state == GHIssueState.OPEN) {
            println("Issue with $gradleID already exists and open")
            return
        } else {
            println("Didn't find latest issue, creating new one")
        }

        val newIssueBuilder = with(repo.createIssue("$trimmedTitle $gradleID")) {
            for (issueLabel in issueLabels.orElse(emptyList()).get()) {
                label(issueLabel)
            }
            body(markdownFile.asFile.get().readText())

        }
        if (dryRun.orElse(false).get()) return
        with(newIssueBuilder.create()) {
            println("Created new issue with $gradleID at $htmlUrl")
        }
    }
}

abstract class CloseGithubIssue() : DefaultTask() {
    init {
        group = "verification"
    }

    @get:Input
    abstract val githubToken: Property<String>

    @get:Input
    abstract val uniqueId: Property<String>

    @get:Input
    @get:Optional
    abstract val reason: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "issue-dry-run", description = "Controls if issue will be created or not")
    abstract val dryRun: Property<Boolean>

    @TaskAction
    fun action() {
        val (github, myself) = Util.buildGithub(githubToken.get())
        val issue = Util.getIssueByTitleId(github.getRepository("DareFox/HowLongToBeat-Proxy-API"), uniqueId.get(), myself.id)

        if (issue == null) {
            println("Can't find any issue to close. Skipping")
            return
        }

        println("Found latest issue at ${issue.htmlUrl}")
        if (issue.state == GHIssueState.CLOSED) {
            println("Issue is already closed with reason: ${issue.stateReason.name}. Skipping")
            return
        }

        if (dryRun.orElse(false).get()) {
            println("This task is in dry run mode, I will not close this issue. Skipping")
            return
        } else {
            val reason = reason.orElse("`reason wasn't provided`").get().trim()
            issue.comment("Closing this issue. Reason: $reason")
            issue.close(GHIssueStateReason.COMPLETED)
            println("Closed this issue as completed with reason:\n\t$reason")
        }
    }
}