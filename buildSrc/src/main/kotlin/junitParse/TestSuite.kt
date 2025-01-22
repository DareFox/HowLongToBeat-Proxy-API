package junitParse

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class TestSuite(
    val name: String,
    val tests: Int,
    val skipped: Int,
    val failures: Int,
    val errors: Int,
    val timestamp: String,
    val hostname: String,
    val time: String,

    @XmlSerialName("testcase")
    val cases: List<TestCase>,

    @XmlSerialName("system-out")
    @XmlElement
    val systemOut: Output,

    @XmlSerialName("system-err")
    @XmlElement
    val systemErr: Output,
)

