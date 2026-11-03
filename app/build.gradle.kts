plugins {
    alias(libs.plugins.stovetime.android.application.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ericwathome.stovetime"

    defaultConfig {
        applicationId = "com.ericwathome.stovetime.start"
    }
}

dependencies {
    implementation(projects.presentation.ui)
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.features.lessons)
    implementation(projects.sdk.timer)
    implementation(libs.timber)
    implementation(projects.features.recipes)
    implementation(projects.features.timers)
    implementation(projects.features.me)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.bundles.navigation3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.androidx.workmanager)
    implementation(libs.work.manager)
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
