plugins {
    alias(libs.plugins.stovetime.android.library)
}

android {
    namespace = "com.ericwathome.stovetime.core.domain"
}

dependencies {
    // modules
    api(projects.core.data)
}
