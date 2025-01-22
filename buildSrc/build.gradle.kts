plugins {
    // The Kotlin DSL plugin provides a convenient way to develop convention plugins.
    // Convention plugins are located in `src/main/kotlin`, with the file extension `.gradle.kts`,
    // and are applied in the project's `build.gradle.kts.backup` files as required.
    `kotlin-dsl`
    alias(libs.plugins.kotlinSerialization)
    idea
}


idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

kotlin {
    jvmToolchain(23)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

dependencies {
    // Add a dependency on the Kotlin Gradle plugin, so that convention plugins can apply it.
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.bundles.kotlinxXml)
    implementation(libs.githubApi)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
