package helpers

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.configureKotlin(extension: CommonExtension, extensionType: ExtensionType) {
    when (extensionType) {
        ExtensionType.Application -> {
            androidApplication {
                compileSdk {
                    version = release(36)
                }
            }
        }
        ExtensionType.Library -> {
            androidLibrary {
                compileSdk {
                    version = release(36)
                }
            }
        }
    }

    configureDefaultConfig(extensionType)
    configureBuildTypes(extensionType)

    extension.run {
        compileOptions.run {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
}