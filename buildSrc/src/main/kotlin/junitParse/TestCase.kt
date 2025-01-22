package junitParse

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class TestCase(
    val name: String,
    val classname: String,
    val time: String,
    @XmlValue val failure: Failure? = null
)