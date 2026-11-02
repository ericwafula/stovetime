---
name: add-feature-module
description: Scaffold a new Gradle module in Stovetime (features/<name>, core/<name>, datasources/<name> or presentation/ui) using the stovetime.* convention plugins, typesafe project accessors, a Koin module, the settings include and app wiring.
when_to_use: User asks to "add a module", "create a new feature", a step needs a module that doesn't exist yet (core:common, core:data, datasources:network …), or a feature has grown enough to deserve its own Gradle module.
---

# Skill: add-feature-module

## Inputs

- Layer + name: `features/<name>`, `core/<name>`, `datasources/<name>` or `presentation/ui` (lowercase, one word where possible, e.g. `features/path`, `features/cook`).

Check `settings.gradle.kts` first. Only `:app` and `:presentation:design` exist at the start; the rest of the map in `architecture` is created by this skill, one module at a time, when a step first needs it. When a lower module is missing (a feature needs `:core:data`, which needs `:core:common`), create it first, bottom up.

## Pick the convention plugin

| Module kind | Plugin alias | Notes |
|---|---|---|
| Compose feature / UI (`features:*`, `presentation:ui`) | `libs.plugins.stovetime.android.library.compose` | applies the library plugin + Compose compiler |
| Android library (`core:common`, `core:data`, `core:domain`, `datasources:*`) | `libs.plugins.stovetime.android.library` | Koin, Timber, coroutines, kotlinx-datetime + JUnit4/Truth/MockK/Turbine test deps come for free |
| Self-contained Android engine (`sdk:*`) | `libs.plugins.stovetime.android.sdk` | no Koin, Timber or project modules |
| Pure Kotlin leaf library with no project dependencies | `libs.plugins.stovetime.jvm.library` | no Android; can't depend on an Android library, so not for `core:domain` |

Plugins are defined in `build-logic/conventions/` and registered in `build-logic/conventions/build.gradle.kts`. Don't re-declare dependencies the plugin already adds. Inside convention plugins, add dependencies with the typed helpers in `helpers/Dependencies.kt` (`implementation(…)`, `testImplementation(…)`, `androidTestImplementation(…)`, `debugImplementation(…)`), never string configuration names like `"implementation"(…)`. Add a helper there when a plugin needs a new configuration.

## Steps

1. **Directory layout**

   ```text
   features/<name>/
     build.gradle.kts
     consumer-rules.pro
     proguard-rules.pro
     .gitignore            # "/build"
     src/main/AndroidManifest.xml
     src/main/java/com/ericwathome/stovetime/features/<name>/di/<Name>Module.kt
     src/main/res/values/strings.xml
     src/test/java/com/ericwathome/stovetime/features/<name>/
   ```

   The manifest is an empty `<manifest xmlns:android="http://schemas.android.com/apk/res/android">`; the namespace comes from Gradle. Only modules that show text get `res/values/strings.xml`.

   Package = `com.ericwathome.stovetime.` + the module path (`features.path`, `core.data`, `datasources.network`). Presentation modules drop the `presentation` group: `:presentation:design` is `com.ericwathome.stovetime.design` and `:presentation:ui` is `com.ericwathome.stovetime.ui`.

2. **`build.gradle.kts`** (feature example)

   ```kotlin
   plugins {
       alias(libs.plugins.stovetime.android.library.compose)
   }

   android {
       namespace = "com.ericwathome.stovetime.features.<name>"
   }

   dependencies {
       // modules
       implementation(projects.core.data)
       implementation(projects.core.domain)
       implementation(projects.presentation.ui)
   }
   ```

   Use typesafe accessors (`projects.core.data`), and keep the `// modules` / `// libraries` comment split. Add libraries via `gradle/libs.versions.toml` aliases only (`libs.bundles.ktor`, `libs.room.runtime`, `libs.datastore.preferences`, `libs.kotlinx.serialization.json`, `libs.work.manager` …).

   `presentation:ui` exposes the design system: `api(projects.presentation.design)`. Features depend on `presentation:ui` only, never on `presentation:design` directly. `:app` moves from `presentation.design` to `presentation.ui` when `:presentation:ui` is created.

3. **`settings.gradle.kts`**: `include(":features:<name>")`.

4. **Koin**: `di/<Name>Module.kt`:

   ```kotlin
   internal val <Name>ViewModelModule =
       module {
           // viewModelOf(::<Screen>ViewModel)
       }

   val <Name>Module =
       module {
           includes(<Name>ViewModelModule, UseCaseModule)
       }
   ```

   Drop `UseCaseModule` until `:core:domain` exists and the feature uses a use case.

5. **App wiring**: `app/build.gradle.kts`: `implementation(projects.features.<name>)`; `app/src/main/java/com/ericwathome/stovetime/di/AppModule.kt`: add `<Name>Module` to `includes(...)`. Core and datasource modules are reached through `CoreDataModule`, which `AppModule` includes once `:core:data` exists.

6. **Dependency graph**: add a `features/<name>/README.md` with a `### Module dependency graph` heading, then run `./gradlew createModuleGraph`. The root `moduleGraphConfig` writes a Mermaid graph under that heading for every module that has a README.

7. Add the first screen with `new-compose-screen`, then register it as a destination or bottom tab (Path, Recipes, Timers, Me) with `add-navigation`. Do the one-time Navigation 3 setup if the app still renders a single screen.

## Dependency direction

```text
app → features:* → core:domain → core:data → datasources:{network,local} → core:common
                 ↘ presentation:ui → presentation:design
```

- Features never depend on `datasources:*` directly, or on other features.
- `core:data` never depends on `core:domain` or UI.
- Shared UI goes in `presentation:design` (design-system components) or `presentation:ui` (other helpers), not in a feature.

## Verify

`./gradlew :features:<name>:assembleDebug` then the `checks` skill.
