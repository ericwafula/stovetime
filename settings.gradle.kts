pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    includeBuild("build-logic")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Stovetime"
include(":app")
include(":presentation:design")
include(":presentation:ui")
include(":core:common")
include(":core:data")
include(":core:domain")
include(":datasources:local")
include(":datasources:network")
include(":sdk:timer")
include(":features:lessons")
include(":features:recipes")
include(":features:timers")
