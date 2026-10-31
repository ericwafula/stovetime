package plugins

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
       target.run {
           pluginManager.run {
               apply("org.jetbrains.kotlin.jvm")
           }

           extensions.configure<JavaPluginExtension> {
               sourceCompatibility = JavaVersion.VERSION_11
               targetCompatibility = JavaVersion.VERSION_11
           }

           extensions.getByType<KotlinJvmExtension>().compilerOptions {
               jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
           }
       }
    }
}