plugins {
    alias(libs.plugins.stovetime.android.library.compose)
}

android {
    namespace = "com.ericwathome.stovetime.features.timers"
}

dependencies {
    // modules
    implementation(projects.core.data)
    implementation(projects.presentation.ui)
}
