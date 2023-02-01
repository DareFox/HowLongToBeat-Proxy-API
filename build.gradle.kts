plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "io.github.darefox"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
//    implementation("org.http4k:http4k-bom:4.37.0.0")
//    implementation("org.http4k:http4k-core")
//    implementation("org.http4k:http4k-server-undertow")
//    implementation("org.http4k:http4k-client-apache")
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}