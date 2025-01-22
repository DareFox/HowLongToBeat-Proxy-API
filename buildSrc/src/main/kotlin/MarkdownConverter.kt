import junitParse.TestCase
import junitParse.TestSuite

object MarkdownConverter {
    fun convert(tests: List<TestSuite>): Sequence<String> {
        require(tests.isNotEmpty()) { "There's no tests to convert to markdown" }
        return sequence {
            for ((index, test) in tests.withIndex()) {
                if (index != 0) {
                    yield("-----------")
                }
                yield(test.toMarkdown())
            }
        }
    }

    private fun markdownDetails(summary: String, content: () -> String): String {
        return """
            |<details>
            |<summary>$summary</summary>
            |
            |${content()}
            |
            |</details>
        """.trimMargin("|")
    }

    private fun TestSuite.toMarkdown(): String {
        val markdownCases = cases.joinToString(separator = "\n") { it.toMarkdown() }
        val markdownStdout = markdownDetails("STDOUT") {
            """
                |```java
                |${systemOut.data}
                |```
            """.trimMargin("|")
        }
        val markdownStderr = markdownDetails("STDERR") {
            """
                |```java
                |${systemErr.data}
                |```
            """.trimMargin("|")
        }


        return """
            |# ${this.name}
            |- Tests: ${this.tests}
            |- Skipped: ${this.skipped}
            |- Failures: ${this.failures}
            |- Errors: ${this.failures}
            |- Timestamp: ${this.timestamp}
            |- Execution time: ${this.time}
            |
            |## Cases
            |$markdownCases
            |
            |## Outputs
            |$markdownStdout
            |
            |$markdownStderr
        """.trimMargin("|")
    }


    private fun TestCase.toMarkdown(): String {
        val emojiStatus = if (this.failure == null) "✅" else "❌"
        val failureDetails = failure?.run {
            """
                |- Error type: `$type`
                |- Message:
                |```java
                |$message
                |```
                |
            """.trimMargin("|")
        }
        return markdownDetails("$name $emojiStatus") {
            """
                |- Classname: $classname
                |- Execution time: $time
                |${failureDetails ?: ""}
            """.trimMargin("|")
        }
    }
}