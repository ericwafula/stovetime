---
name: check-build
description: Compile and assemble Stovetime (whole project or one module), and diagnose Gradle/AGP/KSP/Room and hardcoded-text failures.
when_to_use: User asks to "build", "does it compile", "assemble the APK", or a build broke.
---

# Skill: check-build

## Prerequisites

None. Recipes come from TheMealDB's free public API and progress lives in Room, so there are no keys to set. If a key is ever needed, it goes in the root `local.properties` (never committed) and reaches code through `BuildConfig`.

## Commands

```bash
./gradlew :<module>:compileDebugKotlin   # fastest feedback for one module
./gradlew :app:assembleDebug             # debug APK
./gradlew :app:installDebug              # install on the running emulator or device
./gradlew build                          # all variants + lint + tests + checkHardcodedText
```

Use JDK 21 (the README's requirement; Android Studio's bundled JDK works). Sources compile to Java 11 bytecode via `build-logic`.

## Diagnosing

- **`checkHardcodedText` failed** — a composable passes a string literal to `Text`, `text`, `title`, `description`, `contentDescription`, `supportingText` or `placeholder` in `src/main`. Move it to `res/values/strings.xml` and use `stringResource`. Previews are exempt. The check lives in `build-logic/conventions/src/main/kotlin/helpers/HardcodedText.kt` and runs on every Compose module as part of `check`.
- **Unresolved reference across modules** — check the `dependencies { }` block uses the right `projects.*` accessor and the symbol isn't `internal`.
- **Koin `NoDefinitionFoundException` at runtime** — a new `Impl` or ViewModel wasn't bound in its module, or the module isn't included from `di/AppModule.kt`.
- **Room / KSP errors** (once `:datasources:local` exists) — entity not in `@Database(entities = …)`, DAO `@Query` table name typo (Room lower-cases the class name), or `version` not bumped after a schema change.
- **Build reformatted files** — expected; every module's `preBuild` runs `ktlintFormat`. Review and keep the diff.
- **`build-logic` changes not picked up** — it's an included build; check the plugin's registration and id in `build-logic/conventions/build.gradle.kts` and the `stovetime-*` alias in `gradle/libs.versions.toml`.

Report the first real error (not the cascade) with file:line, fix it, and re-run.
