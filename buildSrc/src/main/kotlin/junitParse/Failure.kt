package junitParse

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class Failure(
    val message: String,
    val type: String,
    @XmlValue val content: String
)