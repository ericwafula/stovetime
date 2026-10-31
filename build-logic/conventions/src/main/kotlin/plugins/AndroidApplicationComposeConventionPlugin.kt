package plugins

import helpers.ExtensionType
import helpers.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("stovetime.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            configureCompose(extensionType = ExtensionType.Application)
        }
    }
}