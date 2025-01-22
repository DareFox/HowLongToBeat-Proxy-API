package junitParse

import kotlin.test.Test

private sealed class ParsedResult {
    data class Error(val exception: Exception): ParsedResult()
    data class Parsed(val value: TestSuite): ParsedResult()
}

class JunitParserTest {
    @Test
    fun parse() {
        val text = JunitParser::class.java.getResource("/junit.xml")!!.readText()
        val result = try {
            ParsedResult.Parsed(JunitParser.parse(text))
        } catch (e: Exception) {
            ParsedResult.Error(e)
        }

        assert(result is ParsedResult.Parsed) { (result as ParsedResult.Error).exception.stackTraceToString() }
        val value = (result as ParsedResult.Parsed).value
        assert(value.name == "LocalServerTest")
        assert(value.tests == 2)
        assert(value.skipped == 0)
        assert(value.failures == 1)
        assert(value.errors == 0)
        assert(value.timestamp.isNotEmpty())
        assert(value.hostname == "THISISHOSTNAME")
        assert(value.time == "4.52")
        assert(value.cases.isNotEmpty())
        assert(value.systemOut.data.isEmpty())
        assert(value.systemErr.data.isNotEmpty())
    }
}