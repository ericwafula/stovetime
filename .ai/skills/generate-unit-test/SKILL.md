---
name: generate-unit-test
description: Write JVM unit tests for Stovetime — JUnit4, Google Truth, MockK, kotlinx-coroutines-test runTest, Turbine for flows, a MainDispatcherRule for ViewModels, snake_case test names and given/when/then comments.
when_to_use: User asks to "write tests", "add unit tests", "cover X", or new logic was added by another skill.
---

# Skill: generate-unit-test

## Stack

JUnit **4**, Google Truth (`assertThat`), MockK, `kotlinx-coroutines-test`, Turbine. Don't add JUnit 5, AssertJ or Mockito.

## Location and naming

- `src/test/java/<same package as the subject>/<Subject>Test.kt`.
- Test names are snake_case sentences: `selecting_a_category_opens_its_guide`.
- Body: `= runTest { // given … // when … // then … }`.
- Shared helpers (like `MainDispatcherRule`) live in `src/test/java/com/ericwathome/stovetime/testing/`. Reuse them; create one there the first time it's needed.

## ViewModel tests

```kotlin
class <Screen>ViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var sut: <Screen>ViewModel

    @Before
    fun setup() {
        sut = <Screen>ViewModel()
    }

    @Test
    fun selecting_a_category_opens_its_guide() =
        runTest {
            sut.event.test {
                // when
                sut.onAction(<Screen>Action.OnSelectCategory("eggs"))

                // then
                assertThat(awaitItem()).isEqualTo(<Screen>Event.OpenGuide("eggs"))
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

## What to cover

- The initial state.
- Every action: the state it produces, or the event it sends.
- Empty and error paths, not just the happy one.

Mock dependencies with `@MockK` and `MockKAnnotations.init(this, relaxed = true)`; stub suspend calls with `coEvery`.

## Running

```bash
./gradlew :app:testDebugUnitTest
```

Reports: `app/build/reports/tests/testDebugUnitTest/index.html`.
