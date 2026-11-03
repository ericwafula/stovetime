---
name: generate-unit-test
description: Write JVM unit tests for Stovetime in its house style — JUnit4, Google Truth, MockK (@MockK relaxed + @InjectMockKs), kotlinx-coroutines-test runTest, Turbine for flows, MainDispatcherRule for ViewModels, Ktor MockEngine for the TheMealDB datasource, snake_case test names and given/when/then comments.
when_to_use: User asks to "write tests", "add unit tests", "cover X", or new logic was added by another skill.
---

# Skill: generate-unit-test

## Stack

JUnit **4** (`org.junit.Test`, `@Before`, `@get:Rule`), Google Truth (`assertThat`), MockK, `kotlinx-coroutines-test`, Turbine. All five are in `gradle/libs.versions.toml`. The `stovetime.android.library` convention plugin puts them on every library module's test classpath; `:app` declares them itself in `app/build.gradle.kts`. Don't add JUnit 5, AssertJ or Mockito.

`ktor-client-mock` (network tests) and `room-testing` are in the catalog but on no test classpath yet. Add them to the module's `testImplementation` when the first test needs them, and tell the user.

## Location and naming

- `<module>/src/test/java/<same package as the subject>/<Subject>Test.kt`.
- Test class: `<ImplClass>Test` (e.g. `GetRecipeRepositoryImplTest`).
- Test names are snake_case sentences: `finishing_a_recipe_awards_its_xp`.
- Body: `= runTest { // given … // when … // then … }`. These three are the only comments allowed.
- Shared helpers (`MainDispatcherRule`, JSON fixtures, `createMockEngine`, test dispatchers) live in a sibling `testing/` package, e.g. `src/test/java/com/ericwathome/stovetime/testing/`. Reuse them; create one there the first time it's needed. Add JSON fixtures as `val successful<Domain>Response = """…""".trimIndent()`.

## By layer

Only `:app` and `:presentation:design` exist today. The datasource, repository and use-case sections apply once `:datasources:*`, `:core:data` and `:core:domain` land; until then most tests are ViewModel tests.

### Network datasource (TheMealDB)

```kotlin
@Test
fun lookup_recipe_returns_a_successful_response() =
    runTest {
        // given
        val httpClient = createHttpClient(createMockEngine(responseBody = successfulRecipeLookupResponse, status = HttpStatusCode.OK))
        val sut = GetRecipeRemoteDatasourceImpl(dispatchers = TestStovetimeDispatchers(testScheduler), client = httpClient)

        // when
        val result = sut(recipeId = "52772")

        // then
        assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
    }
```

Cover: success, TheMealDB's empty answer (`{"meals":null}` for an unknown id or a search with no hits), HTTP error (`HttpStatusCode.InternalServerError` → message contains its `description`), `UnresolvedAddressException` → no-connection error, `SerializationException`, `CancellationException` rethrown, generic exception message.

### Local datasource (Room)

Mock the `internal` DAO with MockK, pass the test dispatchers, assert the success mapping and that `IOException` becomes an error result. Cover the progress the app keeps: completed recipes, XP totals and the weekly streak.

### Repository

```kotlin
@MockK private lateinit var recipeRemoteDatasource: RecipeRemoteDatasource
@MockK private lateinit var recipeLocalDatasource: RecipeLocalDatasource

@Before
fun setup() {
    MockKAnnotations.init(this, relaxed = true)
    sut = GetRecipeRepositoryImpl(recipeRemoteDatasource, recipeLocalDatasource)
}
```

Stub with `coEvery { recipeRemoteDatasource(any()) } returns NetworkResult.Success(dto)`. Cover each caching branch, the empty-cache first-launch path, offline with a cached recipe, and error passthrough. Verify writes with `coVerify` when the cache must be refreshed.

### Use case

`@MockK` repository + `@InjectMockKs` Impl; assert the transformation on success (XP for a finished recipe, whether this week keeps the streak, the next unlocked node on the path) and that the error message passes through untouched.

### ViewModel

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class CookModeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK private lateinit var recipeRepository: RecipeRepository
    private lateinit var sut: CookModeViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        sut = CookModeViewModel(recipeRepository)
    }

    @Test
    fun tapping_next_moves_to_the_following_step() =
        runTest {
            sut.state.test {
                // given
                skipItems(1)

                // when
                sut.onAction(CookModeAction.OnClickNextStep)

                // then
                assertThat(awaitItem().currentStep).isEqualTo(1)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun finishing_the_last_step_opens_the_finish_screen() =
        runTest {
            sut.event.test {
                // when
                sut.onAction(CookModeAction.OnClickFinish)

                // then
                assertThat(awaitItem()).isEqualTo(CookModeEvent.OpenFinish)
            }
        }
}
```

`MainDispatcherRule` swaps `Dispatchers.Main` for an `UnconfinedTestDispatcher`:

```kotlin
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}
```

Copy it into a new module's `testing/` package rather than adding a test-fixtures module. If loading starts on first `state` collection (`onStateSubscription`), start Turbine before asserting.

### Timers and time

Step timers, the weekly streak and "cooked this week" all depend on time. Never read the device clock in a test: pass fixed inputs and drive coroutine time with `advanceTimeBy` / `advanceUntilIdle` on the `runTest` scheduler. See `date-helpers`.

### Worker

If a timer or reminder runs through WorkManager, test the result → `ListenableWorker.Result` mapping and any extracted seam with plain JUnit. Enqueue and end-to-end worker tests need Robolectric + `work-testing` (in the catalog). See the Tests section of `add-worker`.

## What to cover

- The initial state.
- Every action: the state it produces, or the event it sends.
- Empty and error paths, not just the happy one.

## Rules

- Test the `Impl` (it's `internal`, visible from the same module's tests); depend on interfaces for collaborators.
- One behaviour per test; no logic or loops in tests.
- Prefer real mappers and fixtures over mocking pure functions.
- Run `./gradlew :<module>:testDebugUnitTest` and make sure it's green before finishing. Reports: `<module>/build/reports/tests/testDebugUnitTest/index.html`.
