package junitParse

import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML

object JunitParser {
    private val customXML = XML {
        defaultPolicy {
            pedantic = false
            ignoreUnknownChildren()
        }
    }
    fun parse(xmlString: String): TestSuite {
        return customXML.decodeFromString(xmlString)
    }
}