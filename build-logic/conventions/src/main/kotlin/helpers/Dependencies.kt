package helpers

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.implementation(dependency: Any): Dependency? = add("implementation", dependency)

fun DependencyHandler.testImplementation(dependency: Any): Dependency? = add("testImplementation", dependency)

fun DependencyHandler.androidTestImplementation(dependency: Any): Dependency? = add("androidTestImplementation", dependency)

fun DependencyHandler.debugImplementation(dependency: Any): Dependency? = add("debugImplementation", dependency)
