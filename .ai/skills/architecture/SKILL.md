---
name: architecture
description: The Stovetime architecture reference — where code lives today, the module layout it grows into, one-way dependencies, what each layer owns, and the cross-cutting rules (Koin, results, strings, no hardcoded colours). The other skills implement pieces of this.
when_to_use: Deciding where a new file, model or module goes; reviewing code that crosses layers; before starting any feature that spans several layers.
---

# Skill: architecture

This file is the source of truth for structure. If code and this file disagree, ask the user which one is right before changing either.

## Where code lives today

Stovetime is one module, `:app`, until roadmap step 0.1 splits it. Until then:

```text
app/src/main/java/com/ericwathome/stovetime/
├─ MainActivity.kt
├─ StovetimeApplication.kt     starts Koin
├─ di/AppModule.kt             includes every feature's Koin module
└─ <feature>/                  e.g. kitchen, timers, guides
    ├─ <screen>/               <Screen>Screen.kt + <Screen>ViewModel.kt
    └─ di/<Feature>Module.kt
```

## Where it's going

The layout from `.ai/roadmap/ROADMAP.md` §5. Create a module only when a roadmap step asks for it.

```text
:app                  Koin wiring, navigation, MainActivity
:features:<name>      screens and ViewModels, one module per product area
:presentation:design  design system: tokens, theme, components from Figma
:presentation:ui      shared composables
:core:domain          use cases, only when logic is shared
:core:data            repositories
:datasources:local    Room and DataStore
:sdk:timer            timer engine
:core:common          dispatchers, results, pure helpers
```

Dependencies point **down only**. Features never depend on each other or on `datasources:*`. `core:data` never depends on UI.

## Rules

- **One screen = one ViewModel.** Screens never read Room, DataStore or the timer engine directly.
- **Koin only** for dependency injection. Each feature exposes one `val <Feature>Module`, included from `AppModule`.
- **Impls are `internal`.** Only interfaces, models and Koin modules cross module boundaries.
- **Results, not exceptions.** Data calls return a result type; never let an exception reach a ViewModel, and always rethrow `CancellationException`.
- **No hardcoded colours, sizes or fonts in screens.** Use `MaterialTheme.colorScheme`, `MaterialTheme.typography` and shared spacing values. Until `:presentation:design` exists (step 0.2), take exact values from Figma through the Figma MCP and put them in the theme, not in the screen.
- **No hardcoded user-facing text.** Strings go in `res/values/strings.xml`. The build fails on hardcoded `Text("…")` outside previews.
- **Versions** live only in `gradle/libs.versions.toml`. Shared Gradle config lives in `build-logic`.
- **Code explains itself.** No comments unless the code can't say it, and no KDoc on self-explanatory code. Accepted structural comments: `// given` / `// when` / `// then` in tests.

## Where does this go?

| You're adding… | Put it in | Skill |
|---|---|---|
| A screen | `<feature>/<screen>/` | `new-compose-screen` |
| A ViewModel or a new action/state field | next to its screen | `add-viewmodel` |
| Tests | same package under `src/test` | `generate-unit-test` |
| An end-to-end journey | `.maestro/flows/` | `maestro` |

## Don't

- Don't add a second DI framework, HTTP client or database.
- Don't put Android `Context` into ViewModels.
- Don't create modules ahead of the roadmap step that needs them.
