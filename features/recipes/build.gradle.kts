plugins {
    alias(libs.plugins.stovetime.android.library.compose)
}

android {
    namespace = "com.ericwathome.stovetime.features.recipes"
}

dependencies {
    // modules
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.presentation.ui)
}
