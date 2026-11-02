---
name: add-usecase
description: Decide whether business logic earns a use case in Stovetime (only when it's needed in more than one place), then scaffold it in :core:domain — a fun-interface with operator invoke, an internal Impl, registration on a ProgressUseCases-style aggregator, and Koin bindings in UseCaseModule.
when_to_use: User asks to "add a use case"; the same business logic (XP for a finished recipe, weekly streak, next unlocked path node) is about to be written a second time in another ViewModel, screen or worker; or business logic is creeping into a repository.
---

# Skill: add-usecase

Use cases hold business logic that isn't data access and isn't UI: XP rules, streak counting, unlocking the next recipe on the path, combining repositories. They live in `:core:domain` (`com.ericwathome.stovetime.core.domain`), which depends only on `:core:data` (and `:core:common`). If `:core:domain` doesn't exist yet, create it with `add-feature-module` the first time a use case is needed.

## When to add a use case

**A use case exists to share business logic. Add one only when that logic is needed in more than one place.** One caller means no use case.

Example: the XP a cook earns for finishing a recipe (base XP, a bonus for finishing every step timer, a bonus for the first cook of the week). The finish screen shows it, the Path header adds it to the total, and the Me tab lists it per recipe, so it's a `CalculateRecipeXpUseCase` that each of them calls, instead of three copies drifting apart.

### Where business logic goes

| Situation | Put it in |
|---|---|
| Used in one place | That ViewModel, in a `private fun`, as a pragmatic start |
| Needed in a second place | Move it into a use case, and point both callers at it in the same change |
| Mapping or parsing data (TheMealDB DTO → model, Entity → model, splitting instructions into steps) | Repository mappers in `core:data/mappers`. That's data work, so it never goes in the UI layer |
| Any other business logic | Never a repository |

### Start in the ViewModel, promote when it's reused

Business logic in a ViewModel is a pragmatic starting point, not where it should stay. Keep it small, pure and in its own `private fun` so it lifts out cleanly.

Before writing business logic, check whether it's needed elsewhere. Search the features and workers for the same rule, and look at the roadmap step and its spec for other screens that need it (Path, finish screen and Me all show XP and the streak). If it's already somewhere else, or the work in hand adds a second caller:

1. Create the use case with the steps below, moving the logic over as it is.
2. Replace every existing copy with a call to the use case. Don't leave the old private function behind.
3. Move or add the tests in `:core:domain`.

When you leave logic in a ViewModel because it has one caller, say so in your report. That way it's a recorded choice the reviewer can see.

## Inputs

- `<Name>`: verb phrase in PascalCase (e.g. `GetWeeklyStreak`).
- `<Feature>`: the aggregator group (e.g. `Progress`).
- The repositories it needs.

## Files

### 1. Use case — `useCase/<Name>UseCase.kt`

```kotlin
fun interface GetWeeklyStreakUseCase {
    suspend operator fun invoke(): DataResult<Int>
}

internal class GetWeeklyStreakUseCaseImpl(
    private val repository: ProgressRepository,
) : GetWeeklyStreakUseCase {
    override suspend fun invoke(): DataResult<Int> =
        repository
            .getCompletedRecipes()
            .map { completed ->
                weeklyStreak(
                    cookedWeeks = completed.map { it.completedAt.asLocalDate.weekStart() }.toSet(),
                    thisWeek = Today.weekStart(),
                )
            }
}
```

- Transform with `DataResult.map { }`; never unwrap and re-wrap by hand.
- Week and day rules (`weekStart()`, `weeklyStreak(...)`) are pure helpers in `core:common/helpers`, and the current day is `Today` from `:core:data`'s `helpers/DateHelpers.kt` (a computed getter, read fresh each time). Reuse them before adding; don't declare another clock (`date-helpers`).

### 2. Aggregator — `useCase/<Feature>UseCases.kt`

```kotlin
interface ProgressUseCases {
    val getWeeklyStreak: GetWeeklyStreakUseCase
    val calculateRecipeXp: CalculateRecipeXpUseCase
}

internal class ProgressUseCasesImpl(
    override val getWeeklyStreak: GetWeeklyStreakUseCase,
    override val calculateRecipeXp: CalculateRecipeXpUseCase,
) : ProgressUseCases
```

Add a `val` if the aggregator exists already.

### 3. Koin — `di/UseCaseModule.kt`

```kotlin
internal val ProgressUseCaseModule =
    module {
        // parent
        singleOf(::ProgressUseCasesImpl) {
            bind<ProgressUseCases>()
        }
        // children
        singleOf(::GetWeeklyStreakUseCaseImpl) {
            bind<GetWeeklyStreakUseCase>()
        }
    }
```

Add any new module to `UseCaseModule.includes(...)`. Feature modules pull in `UseCaseModule` from their own Koin module (`includes(<Name>ViewModelModule, UseCaseModule)`, see `add-feature-module`).

## Rules

- No Android framework, Compose, Ktor or Room imports in `:core:domain`.
- Use cases are stateless and one-shot; streams (live XP total, completed recipes) stay in repositories.
- A use case doesn't need a repository. Pure rules like `CalculateRecipeXpUseCase` take their inputs as parameters, and can drop `suspend` when there's no I/O, so the finish screen can call it directly.
- Only logic needed in two or more places becomes a use case (see "When to add a use case"). Being complex or "domain-ish" isn't enough on its own.
- If the logic is a single repository call with no transformation, call the repository from the ViewModel instead; don't add pass-through use cases.
- Test in `core/domain/src/test/java/com/ericwathome/stovetime/core/domain/useCase/<Name>UseCaseImplTest.kt` with `@MockK` repository + `@InjectMockKs` Impl, `MockKAnnotations.init(this, relaxed = true)`, and `// given` / `// when` / `// then` comments. Cover success-transform and error passthrough; for streak rules also cover a week boundary and a skipped week.
- Finish with the `checks` skill.

## Reference

None yet. When the first use case lands, list its file, aggregator, Koin module and test here.
