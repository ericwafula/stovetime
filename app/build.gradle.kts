plugins {
    alias(libs.plugins.stovetime.android.application.compose)
}

android {
    namespace = "com.ericwathome.stovetime"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
}
