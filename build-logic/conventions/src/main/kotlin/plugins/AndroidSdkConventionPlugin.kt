package plugins

import helpers.ExtensionType
import helpers.androidLibrary
import helpers.androidTestImplementation
import helpers.configureKotlin
import helpers.implementation
import helpers.libs
import helpers.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** A self-contained library: no Koin, Timber or app modules, so an SDK can't reach into the app. */
class AndroidSdkConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("com.android.library")
            androidLibrary {
                configureKotlin(this, ExtensionType.Library)
            }

            dependencies {
                implementation(libs.findLibrary("androidx.core.ktx").get())
                implementation(libs.findLibrary("coroutines.android").get())
                testImplementation(libs.findLibrary("coroutines.test").get())
                testImplementation(libs.findLibrary("junit").get())
                testImplementation(libs.findLibrary("google.truth").get())
                androidTestImplementation(libs.findLibrary("coroutines.test").get())
                androidTestImplementation(libs.findLibrary("androidx.junit").get())
                androidTestImplementation(libs.findLibrary("androidx.test.runner").get())
                androidTestImplementation(libs.findLibrary("google.truth").get())
            }
        }
    }
}
