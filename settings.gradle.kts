rootProject.name = "HLTB-API"

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(":hltb-scraping")
include(":server")
include(":cache")
include(":utils")