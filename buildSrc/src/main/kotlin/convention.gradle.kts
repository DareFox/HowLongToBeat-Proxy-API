plugins {
    kotlin("jvm")
    idea
}

group = "io.github.darefox"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(23)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}