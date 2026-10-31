package helpers

import org.gradle.api.Project

internal fun Project.configureDefaultConfig(extensionType: ExtensionType) {
    when (extensionType) {
        ExtensionType.Application -> {
            androidApplication {
                defaultConfig {
                    applicationId = "com.ericwathome.stovetime"
                    minSdk = 29
                    targetSdk = 36
                    versionCode = 1
                    versionName = "1.0"

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }
        }

        ExtensionType.Library -> {
            androidLibrary {
                defaultConfig {
                    minSdk = 29

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
            }
        }
    }
}
