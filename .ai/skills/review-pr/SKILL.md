---
name: review-pr
description: Review a Stovetime branch, PR or local diff against the project's own conventions — correctness first, then architecture boundaries, error/result handling, Koin wiring, strings and theme, tests and the recurring-defect checklist. Read-only; produces findings, never approves or merges.
when_to_use: User asks to review a PR, a branch, staged changes or "my diff" before committing or pushing.
---

# Skill: review-pr

Ground every finding in this repo's conventions: `CLAUDE.md`, the `architecture` skill plus the building skills (`add-datasource`, `add-repository`, `add-usecase`, `add-viewmodel`, `new-compose-screen`, `add-worker`, `add-navigation`). Don't invent rules.

## Scope

- Branch: `git diff <base>...HEAD` (three-dot: only what the branch introduces). The base is `main` unless the user says otherwise.
- Staged: `git diff --cached`. PR: `gh pr diff <n>`.

## Priority order

1. **Correctness.** Logic bugs, missing edge cases (TheMealDB returning `{"meals":null}`, a recipe with no steps or no timed steps, first launch with empty progress, the streak across a week boundary, XP awarded twice for the same recipe), wrong coroutine/Flow use (collecting in `init`, leaking scopes, missing `stateIn`), timers that stop or drift when the screen leaves, lifecycle leaks.
2. **Boundaries.** Features importing `datasources:*`, Ktor, Room or `androidx.work`. Feature → feature dependencies. `Context` in ViewModels or use cases. Screens reading Room, DataStore or the timer engine directly. Upward Gradle dependencies. A module created ahead of the roadmap step that needs it.
3. **Results and errors.** Exceptions escaping datasources. Hand-rolled `when` over the network/local result types instead of the shared mapper. Results not bridged with `onSuccess`/`onError` in ViewModels.
4. **Wiring.** A new `Impl` or ViewModel without a Koin binding, or a feature module not included from `di/AppModule.kt`. A worker not registered with `workerOf`. A destination key not handled in the nav host or missing from the bottom bar (Path, Recipes, Timers, Me).
5. **UI rules.** Hardcoded colours, sizes or fonts in screens instead of `StovetimeTheme` tokens and `:presentation:design` components. One-off effects in `State` instead of the `event` channel. `collectAsState()` instead of `collectAsStateWithLifecycle()`. Touch targets under 48dp.
6. **Tests.** New or changed logic without a test at the right layer, and tests that don't cover the empty or error path.

## Recurring-defect checklist (always scan added lines in `src/main`)

| Check | Why |
|---|---|
| `catch (e: Throwable)`, or `catch (e: Exception)` without rethrowing `CancellationException` | swallows cancellation, so coroutines, timers and workers can't stop |
| Public `class`/`fun` in a feature other than `<Screen>Route` and the Koin module | leaks internals and breaks encapsulation |
| A new `Impl` that isn't `internal` | the public surface should be interfaces only |
| `Dispatchers.IO`/`Main` used directly outside the dispatchers implementation in `:core:common` | untestable, so inject the dispatchers interface |
| A hard-coded version or Maven coordinate in a `build.gradle.kts` | versions belong in `gradle/libs.versions.toml` |
| `enqueue(` without `Unique` for WorkManager | duplicates work on every trigger |
| Keys or `local.properties` values in source | must come from `BuildConfig`, never committed |
| `Color(0xFF…)`, `.sp`/`.dp` literals for theme values in a screen | belongs in the theme |
| `Log.*` / `println` | use Timber |
| `getOrNull`/`runCatching` that drops the error silently | log it with Timber or surface it as an error |

Skip anything ktlint or `checkHardcodedText` enforces.

- **Comments that restate the code, or explain code that should have been clearer.** Ask for a better name or a smaller function instead, and remove the comment. Keep only comments that give a *why* the code can't express, plus `// given // when // then` in tests.
- **AI attribution** anywhere in code, docs or commit messages — must go.
- **Time:** the system clock read directly in logic that decides streaks, "this week" or timer state, or a time-dependent `val` without a getter (frozen at load). See `date-helpers`.

## Report

- Each finding gets `file:line`, the convention it breaks and the concrete fix.
- Split findings into **Must fix** (correctness, boundaries, missing wiring) and **Consider** (improvements).
- If nothing material is wrong, say so in one line.
- Never approve, merge or post comments unless asked. For an existing PR's comments, use `github-review-pr`.
