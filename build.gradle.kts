val coroutinesVersion = "1.6.4"
val http4kVersion = "4.37.0.0"

plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "io.github.darefox"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    testImplementation(kotlin("test"))

    // http4k
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-server-jetty:$http4kVersion")
    implementation("org.http4k:http4k-client-apache:$http4kVersion")
    implementation("org.http4k:http4k-format-kotlinx-serialization:$http4kVersion")

    // loggers
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // dependency for mutex
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // html parser
    implementation("org.jsoup:jsoup:1.15.4")

    implementation("org.apache.commons:commons-collections4:4.1")
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.jar {
    manifest.attributes["Main-Class"] = "io.github.darefox.hltbproxy.MainKt"
}

application {
    mainClass.set("io.github.darefox.hltbproxy.MainKt")
}