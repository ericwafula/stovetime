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

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.library")
            }
            androidLibrary {
                configureKotlin(this, ExtensionType.Library)

                buildFeatures {
                    buildConfig = true
                }
            }

            dependencies {
                implementation(platform(libs.findLibrary("koin-bom").get()))
                implementation(libs.findBundle("koin").get())
                implementation(libs.findLibrary("timber").get())
                implementation(libs.findLibrary("androidx.core.ktx").get())
                implementation(libs.findLibrary("coroutines.core").get())
                implementation(libs.findLibrary("kotlinx.datetime").get())
                testImplementation(libs.findLibrary("coroutines.test").get())
                testImplementation(libs.findLibrary("junit").get())
                testImplementation(libs.findLibrary("google.truth").get())
                testImplementation(libs.findLibrary("mockk").get())
                testImplementation(libs.findLibrary("turbine").get())
                androidTestImplementation(libs.findLibrary("coroutines.test").get())
                androidTestImplementation(libs.findLibrary("androidx.junit").get())
                androidTestImplementation(libs.findLibrary("androidx.test.runner").get())
                androidTestImplementation(libs.findLibrary("google.truth").get())
                androidTestImplementation(libs.findLibrary("mockk").get())
                androidTestImplementation(libs.findLibrary("turbine").get())
            }
        }
    }
}
