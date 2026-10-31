package helpers

import org.gradle.api.Project

internal fun Project.configureBuildTypes(extensionType: ExtensionType) {
    when (extensionType) {
        ExtensionType.Application -> {
            androidApplication {
                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }
        }
        ExtensionType.Library -> {
            androidLibrary {
                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }
        }
    }
}