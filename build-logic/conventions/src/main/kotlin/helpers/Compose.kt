package helpers

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.configureCompose(extensionType: ExtensionType) {
    when (extensionType) {
        ExtensionType.Application -> {
            androidApplication {
                buildFeatures {
                    compose = true
                }
            }
        }
        ExtensionType.Library -> {
            androidLibrary {
                buildFeatures {
                    compose = true
                }
            }
        }
    }

    configureHardcodedTextCheck()

    dependencies {
        implementation(platform(libs.findLibrary("androidx.compose.bom").get()))
        implementation(libs.findLibrary("androidx.compose.ui").get())
        implementation(libs.findLibrary("androidx.compose.ui.graphics").get())
        implementation(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
        implementation(libs.findLibrary("androidx.compose.material3").get())
        implementation(platform(libs.findLibrary("koin-bom").get()))
        implementation(libs.findBundle("koin-compose").get())
        androidTestImplementation(libs.findLibrary("androidx.junit").get())
        androidTestImplementation(libs.findLibrary("androidx.espresso.core").get())
        androidTestImplementation(platform(libs.findLibrary("androidx.compose.bom").get()))
        androidTestImplementation(libs.findLibrary("androidx.compose.ui.test.junit4").get())
        debugImplementation(libs.findLibrary("androidx.compose.ui.tooling").get())
        debugImplementation(libs.findLibrary("androidx.compose.ui.test.manifest").get())
    }
}