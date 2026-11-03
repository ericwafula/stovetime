plugins {
    alias(libs.plugins.stovetime.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ericwathome.stovetime.datasources.network"

    defaultConfig {
        buildConfigField("String", "MEALDB_BASE_URL", "\"https://www.themealdb.com/api/json/v1/1/\"")
    }
}

dependencies {
    // modules
    implementation(projects.core.common)

    // libraries
    implementation(libs.bundles.ktor)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.ktor.client.mock)
}
