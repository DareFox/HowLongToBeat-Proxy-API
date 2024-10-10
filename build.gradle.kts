val coroutinesVersion = "1.6.4"
val http4kVersion = "5.26.0.0"

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
    idea
}

group = "io.github.darefox"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

dependencies {
    testImplementation(kotlin("test"))

    // http4k
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-server-jetty:$http4kVersion")
    implementation("org.http4k:http4k-client-apache:$http4kVersion")
    implementation("org.http4k:http4k-format-kotlinx-serialization:$http4kVersion")

    // loggers
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // dependency for mutex
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // html parser
    implementation("org.jsoup:jsoup:1.18.1")

    implementation("org.apache.commons:commons-collections4:4.4")
}


tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest.attributes["Main-Class"] = "io.github.darefox.hltbproxy.MainKt"
}

application {
    mainClass.set("io.github.darefox.hltbproxy.MainKt")
}