plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.kgp)
    compileOnly(libs.agp)
}

gradlePlugin {
    plugins {
        fun createPlugin(
            name: String,
            id: String,
            implementationClass: String,
        ) {
            create(name) {
                this.id = id
                this.implementationClass = implementationClass
            }
        }

        createPlugin(
            name = "androidAplication",
            id = "stovetime.android.application",
            implementationClass = "plugins.AndroidApplicationConventionPlugin",
        )

        createPlugin(
            name = "androidLibrary",
            id = "stovetime.android.library",
            implementationClass = "plugins.AndroidLibraryConventionPlugin",
        )

        createPlugin(
            name = "androidSdk",
            id = "stovetime.android.sdk",
            implementationClass = "plugins.AndroidSdkConventionPlugin",
        )

        createPlugin(
            name = "androidAplicationCompose",
            id = "stovetime.android.application.compose",
            implementationClass = "plugins.AndroidApplicationComposeConventionPlugin",
        )

        createPlugin(
            name = "androidLibraryCompose",
            id = "stovetime.android.library.compose",
            implementationClass = "plugins.AndroidLibraryComposeConventionPlugin",
        )

        createPlugin(
            name = "jvmLibrary",
            id = "stovetime.jvm.library",
            implementationClass = "plugins.JvmLibraryConventionPlugin",
        )
    }
}
