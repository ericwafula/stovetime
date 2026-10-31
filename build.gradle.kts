import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.modulegraph)
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        enableExperimentalRules.set(true)
        additionalEditorconfig.set(
            mapOf(
                "ktlint_standard_package-naming" to "disabled",
            ),
        )
        reporters {
            reporter(ReporterType.JSON)
        }
        filter {
            exclude {
                projectDir
                    .toURI()
                    .relativize(it.file.toURI())
                    .path
                    .contains("/generated/")
            }
            exclude {
                projectDir
                    .toURI()
                    .relativize(it.file.toURI())
                    .path
                    .contains("/build/")
            }
            exclude {
                projectDir
                    .toURI()
                    .relativize(it.file.toURI())
                    .path
                    .contains("ParserData")
            }
        }
    }
}

subprojects {
    afterEvaluate {
        if (tasks.names.contains("ktlintFormat")) {
            tasks.matching { it.name == "preBuild" }.configureEach {
                dependsOn("ktlintFormat")
            }
        }
    }
}

moduleGraphConfig {
    subprojects.forEach { p ->
        val readme = p.file("README.md")

        val modulePath = p.path.replace(":", "\\:")

        if (readme.exists()) {
            graph(
                readmePath = readme.absolutePath,
                heading = "### Module dependency graph",
            ) {
                showFullPath = false
                rootModulesRegex = "^$modulePath$"
                includeIsolatedModules = true
            }
        }
    }
}
