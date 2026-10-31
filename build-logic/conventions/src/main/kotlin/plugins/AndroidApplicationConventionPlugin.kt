package plugins

import helpers.ExtensionType
import helpers.androidApplication
import helpers.androidTestImplementation
import helpers.configureKotlin
import helpers.implementation
import helpers.libs
import helpers.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.application")
            }

            androidApplication {
                configureKotlin(this, ExtensionType.Application)

                buildFeatures {
                    buildConfig = true
                }
            }

            dependencies {
                implementation(platform(libs.findLibrary("koin-bom").get()))
                implementation(libs.findBundle("koin").get())
                implementation(libs.findLibrary("timber").get())
                implementation(libs.findLibrary("androidx.core.ktx").get())
                testImplementation(libs.findLibrary("junit").get())
                androidTestImplementation(libs.findLibrary("androidx.junit").get())
            }
        }
    }
}