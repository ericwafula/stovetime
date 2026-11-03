plugins {
    alias(libs.plugins.stovetime.android.library)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ericwathome.stovetime.datasources.local"
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // modules
    implementation(projects.core.common)

    // libraries
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.room.runtime)
    implementation(libs.datastore.preferences)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.room.testing)
}
