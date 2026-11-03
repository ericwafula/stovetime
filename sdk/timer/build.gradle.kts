plugins {
    alias(libs.plugins.stovetime.android.sdk)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ericwathome.stovetime.sdk.timer"
}

dependencies {
    // libraries
    implementation(libs.kotlinx.serialization.json)
}
