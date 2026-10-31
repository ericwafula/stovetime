package helpers

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

fun Project.androidApplication(block: ApplicationExtension.() -> Unit) {
    extensions.configure(block)
}

fun Project.androidLibrary(block: LibraryExtension.() -> Unit) {
    extensions.configure(block)
}

val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")