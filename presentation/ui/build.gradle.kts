plugins {
    alias(libs.plugins.stovetime.android.library.compose)
}

android {
    namespace = "com.ericwathome.stovetime.ui"
}

dependencies {
    // modules
    api(projects.presentation.design)
    api(projects.core.common)

    // libraries
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.activity.compose)
}
