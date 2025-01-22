import junitParse.JunitParser
import kotlin.test.Test

class MarkdownConverterTest {
    @Test
    fun convert() {
        val text = JunitParser::class.java.getResource("/junit.xml")!!.readText()
        val testSuites = listOf(JunitParser.parse(text))
        MarkdownConverter.convert(testSuites).also { println(it.toList().joinToString("\n")) }
    }
}