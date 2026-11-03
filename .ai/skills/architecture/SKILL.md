---
name: architecture
description: The Stovetime architecture reference — where code lives today, the module map it grows into, one-way dependencies, what each layer owns, naming and visibility rules, and a playbook for placing new code or fixing code that drifted. The other scaffolding skills implement pieces of this.
when_to_use: Deciding where a new file, model or module goes; reviewing or refactoring code that crosses layers; "align X with the architecture"; before starting any feature that spans several layers.
---

# Skill: architecture

This file is the source of truth for structure. If code and this file disagree, either the code is migration debt or this file needs updating. Ask the user which before changing either.

## Where code lives today

Two modules exist: `:app` (`MainActivity`, `StovetimeApplication` starting Koin, `di/AppModule.kt`) and `:presentation:design` (the design system). Every other module below is created by `add-feature-module` the first time a step needs it. Don't create modules ahead of that.

## Module map

```text
app
 ├─ features:<name>              (Compose UI + ViewModels, one Gradle module per product area: path, recipes, cook, timers, me)
 │   ├─ core:domain              (use cases, only when logic is shared)
 │   │   └─ core:data            (repositories, data models, mappers, workers, date helpers)
 │   │       ├─ datasources:network   (Ktor client for TheMealDB, DTOs, NetworkResult)
 │   │       └─ datasources:local     (Room + DataStore, Entity internal / Cache public, LocalResult)
 │   └─ presentation:ui          (MviViewModel, ViewState, shared composables and text helpers; re-exports design)
 │       └─ presentation:design  (design system: tokens, StovetimeTheme, Stove* components, PathNode, chips)
 └─ build-logic/conventions      (stovetime.* Gradle plugins)

core:common                      (DataResult/DataError, StovetimeDispatchers, pure helpers; the bottom of the graph)
```

Dependencies point **down only**:

- `core:common` depends on no other project module. Any module may depend on it.
- `datasources:*` know nothing about `core:*` except `core:common`.
- `presentation:design` depends on no other project module; `presentation:ui` exposes it with `api(...)`.
- `core:data` doesn't depend on `core:domain` or UI.
- `core:domain` depends only on `core:data` (and `core:common`).
- Features depend on `core:data`, `core:domain` and `presentation:ui`, and never on `datasources:*` or another feature.
- `app` wires Koin and hosts navigation (bottom tabs Path, Recipes, Timers, Me).
- A self-contained Android engine (for example the cook-timer service, if it is split out) goes in an `sdk:<name>` module on the `stovetime.android.sdk` plugin: no Koin, Timber or project modules, a small contract, configured by `:app`. None exists yet.

There is no backend of our own and no accounts. Recipes are read from TheMealDB (read-only JSON); progress (XP, streak, completed recipes) lives on the phone in Room.

## What each layer owns

| Layer | Owns | Public surface | Never |
|---|---|---|---|
| `datasources:network` | `HttpClient`, `Route`, DTOs, `safeApiCall` | `fun interface`s returning `NetworkResult<Dto>`, DTOs | throws, core types |
| `datasources:local` | Room DB, DAOs, Entities, DataStore, `safeDatabaseCall` | `fun interface`s returning `LocalResult<Cache>`, Cache models, `PreferenceStore` groups | exposes Entity or DAO |
| `core:data` | repositories, `CachingStrategy`, data models, mappers, workers, `Now`/`Today` | repository `fun interface`s + parent aggregators, models, `mapToData` | Ktor/Room types in signatures |
| `core:domain` | use cases (XP rules, streak counting, path progress) | use-case `fun interface`s + aggregators | Android/Compose imports |
| `core:common` | `DataResult`, `DataError`, `StovetimeDispatchers`, pure helpers shared across layers | helpers and their Koin module | Android UI, Ktor, Room, any project dependency |
| `presentation:ui` | `MviViewModel`, `ViewState`, shared composables, date/number text helpers | composables and helpers, plus design via `api(...)` | feature logic |
| `presentation:design` | colours, type, spacing, shapes, `StovetimeTheme`, `Stove*` components, `PathNode`, `StatChip`, `TimerChip`, `Pill`, icons | `StovetimeTheme`, tokens, components | feature logic, `core:*` or `features:*` imports |
| `features:<name>` | screens, ViewModels, feature-only helpers | `<Screen>Route` composables and the Koin module | data access, WorkManager |
| `app` | `StovetimeApplication`, `MainActivity`, `AppModule`, navigation | — | business logic |

## Cross-cutting conventions

- **One screen = one ViewModel.** Screens never read Room, DataStore, the network or the timer engine directly.
- **One operation = one `fun interface` with `suspend operator fun invoke`.** Related operations are grouped by a parent interface of `val`s (`RecipesRemoteDatasource`, `RecipesRepository`, `ProgressUseCases`). Consumers inject the parent.
- **Impls are `internal`.** Only interfaces, models and Koin modules cross module boundaries.
- **Results, not exceptions.** `NetworkResult` / `LocalResult` → `DataResult` (via `mapToData`) → `ViewState`. Never let an exception escape a datasource, and always rethrow `CancellationException`.
- **Models per layer.** `Dto` (network) / `Entity` (Room, internal) / `Cache` (local, public) → data model in `core:data/model` → shown directly by UI. Mappers live in the layer that knows both sides.
- **Koin only.** Every module exposes one public `val <Name>Module` built from `internal` sub-modules with `// parent` and `// children` bindings. `AppModule` includes `CoreDataModule` and each feature module.
- **Dispatchers** come from the injected `StovetimeDispatchers`, never `Dispatchers.IO` directly (except inside its Impl).
- **Background work** is WorkManager in `core:data` (`add-worker`). A cook timer that must ring on time is a foreground service with an ongoing notification, not a worker.
- **Process-long work** uses an injected application scope (`SupervisorJob` + main immediate) from `core:common`, never `GlobalScope` or a hand-built `CoroutineScope(...)`. Never launch coroutines from `StovetimeApplication` directly.
- **Dates and times** go through the shared helpers (`date-helpers`): `Now` and `Today` in `core:data/helpers/DateHelpers.kt` (never an injected `Clock`), device time zone only, the streak counted per calendar week; pure date rules in `core:common/helpers`; text for screens in `presentation:ui/helpers`. Feature modules never use `DateTimeFormatter`, `NumberFormat`, `SimpleDateFormat` or `DateUtils` themselves.
- **No hardcoded colours, sizes or fonts in screens.** Use `StovetimeTheme.colors`, `.typography`, `.spacing` and `.shapes`, never `MaterialTheme` or Material components. Missing values come from Figma through the Figma MCP and go into `:presentation:design`, not into the screen.
- **No hardcoded user-facing text.** Strings go in `res/values/strings.xml` of the module that shows them. The build fails on hardcoded `Text("…")` outside previews.
- **Code explains itself; comments are the exception.** Make code self-documenting through names, small functions and types. Code that needs a comment to be understood is usually the problem: rename, extract or simplify it first. Write a comment only when the code really can't say it, such as a non-obvious *why* (a platform quirk, a workaround, a rule from outside the code). Never restate what the code does, and no KDoc on self-explanatory declarations. KDoc (`/** … */`) counts as a comment. The only accepted structural comments are `// given` / `// when` / `// then` in tests, `// parent` / `// children` and `// <thing> DAOs` in Koin modules, and `// modules` / `// libraries` in build files. Before opening a PR, list the comments the diff adds and remove every one that isn't one of those or a real non-obvious why.
- **Versions** live only in `gradle/libs.versions.toml`. Shared Gradle config lives in `build-logic` convention plugins.
- **No AI attribution** anywhere: code, comments, commits, docs.

## Where does this go?

| You're adding… | Put it in | Skill |
|---|---|---|
| A TheMealDB endpoint or a Room table / preference | `datasources:network` / `datasources:local` | `add-datasource` |
| Combining remote + cache, mapping to UI models | `core:data` repository | `add-repository` |
| Business logic needed in 2+ places (XP for a finish, weekly streak, next unlocked path node); single-use logic stays in its ViewModel until a second caller appears | `core:domain` use case | `add-usecase` |
| Background/periodic job (prefetching path recipes for offline cook mode) | `core:data/workers` | `add-worker` |
| Current time, today, week boundaries, date or duration text | `core:data`, `core:common`, `presentation:ui` helpers | `date-helpers` |
| A screen | existing or new `features:<name>` | `new-compose-screen`, `add-viewmodel` |
| A new product area | new `features:<name>` module | `add-feature-module` |
| Moving between screens or tabs | `app` navigation | `add-navigation` |
| A design-system token or component (used by 2+ features, has a Figma frame) | `presentation:design` | `implement-design` |
| A non-design UI helper used by 2+ features (e.g. `MviViewModel`, image loading) | `presentation:ui` | — |
| A pure helper used by 2+ modules (e.g. dispatchers, week maths) | `core:common` | — |
| Tests | same package under `src/test` | `generate-unit-test` |
| An end-to-end journey | `.maestro/flows/` | `maestro` |

## Migrating a slice that drifted

Work on one domain per PR, and keep each step compiling.

1. **Datasources.** Split multi-operation classes into `fun interface`s. Make sure the Entity is `internal` and every call goes through `safeApiCall` / `safeDatabaseCall`.
2. **Repository.** Use an interface + `internal` Impl with an explicit `CachingStrategy`, no framework imports, and mappers in `core:data/mappers`.
3. **Use case.** Only if the logic is needed in more than one place. Otherwise it stays in the ViewModel, which calls the repository directly.
4. **Feature.** Use the `Route` → `Screen` → `Content` split, an `MviViewModel` with `ViewState` fields, and make only `Route` public.
5. **Koin.** Use parent/children bindings, reachable from `AppModule`.
6. **Dependencies.** Run `./gradlew :<module>:dependencies --configuration debugRuntimeClasspath` and check for arrows pointing up or sideways.
7. **Verify** with `checks`, then commit with `commit-convention` (for example `chore/refactor: aligns recipes datasources with architecture`).

## Don't

- Don't add a second DI framework, HTTP client or database.
- Don't add a backend, accounts, analytics or crash-reporting SDKs; none are in scope.
- Don't add feature → feature dependencies. Share through `core:*`.
- Don't put Android `Context` into ViewModels or use cases. Wrap it behind a `fun interface` in `core:data` (see `add-worker`'s scheduler).
- Don't create modules ahead of the step that needs them.
- Don't refactor several domains at once.
