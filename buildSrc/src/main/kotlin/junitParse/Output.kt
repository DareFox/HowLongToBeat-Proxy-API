package junitParse

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlCData
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class Output(
    @XmlCData @XmlValue val data: String
)