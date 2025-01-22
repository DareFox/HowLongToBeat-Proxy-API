plugins {
    id("convention")
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(libs.bundles.http4kClient)
    implementation(libs.bundles.logging)
    implementation(libs.bundles.kotlinx)
    implementation(libs.jsoup)
}