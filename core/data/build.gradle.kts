plugins {
    alias(libs.plugins.stovetime.android.library)
}

android {
    namespace = "com.ericwathome.stovetime.core.data"
}

dependencies {
    // modules
    api(projects.core.common)
    implementation(projects.datasources.local)
    api(projects.sdk.timer)
    implementation(projects.datasources.network)

    // libraries
    implementation(libs.work.manager)
    implementation(libs.koin.androidx.workmanager)
}
