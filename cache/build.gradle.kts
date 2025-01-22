plugins {
    id("convention")
}

dependencies {
    implementation(libs.kotlinXcoroutines)
    implementation(libs.commonCollections)
    implementation(project(":utils"))
}