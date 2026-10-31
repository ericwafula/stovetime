package plugins

import helpers.ExtensionType
import helpers.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("stovetime.android.library")
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            configureCompose(extensionType = ExtensionType.Library)
        }
    }
}