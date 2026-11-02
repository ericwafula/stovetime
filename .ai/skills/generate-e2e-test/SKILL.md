---
name: generate-e2e-test
description: Write hermetic instrumented end-to-end UI tests for Stovetime — Compose testing on the real MainActivity or a feature host, Robot pattern with test tags, Koin overrides that fake TheMealDB (Ktor MockEngine) and time, in-memory Room, no sleeps, and Gradle Managed Devices for headless runs.
when_to_use: User asks for an E2E / instrumented / UI test of a user flow (path → recipe → cook mode → finish screen, step timers, offline with cached recipes, switching tabs), or wants to reproduce a bug end-to-end before fixing it.
---

# Skill: generate-e2e-test

An E2E test drives a real user flow through real Compose UI, with every external boundary pinned to a deterministic fake. It isn't a ViewModel test (that's `generate-unit-test`), it isn't a black-box device journey (that's `maestro`), and it isn't a screenshot test.

## Pick the layer

| Scope | Rule | Location |
|---|---|---|
| One feature flow | `createAndroidComposeRule<ComponentActivity>()` + `setContent { StovetimeTheme { <Screen>Route() } }` | `features/<name>/src/androidTest/java/.../e2e/` (in `app/src/androidTest/` until feature modules exist) |
| Full app journey | `createAndroidComposeRule<MainActivity>()` | `app/src/androidTest/java/com/ericwathome/stovetime/e2e/` |

## Boundaries to fake

| Boundary | How |
|---|---|
| Network (TheMealDB) | Override `single<HttpClient>` with `createHttpClient(MockEngine { request -> … })`. Route on the path: `lookup.php` → one recipe, `search.php` → search results, `filter.php` / `categories.php` → lists, each to its JSON fixture. Answer `{"meals":null}` for the empty case. `createHttpClient` will be `internal`, so add a small public test hook in `:datasources:network` (for example `fun testHttpClient(engine: HttpClientEngine): HttpClient`) only when the first E2E test needs it, and tell the user. |
| Room | Override the database binding with `Room.inMemoryDatabaseBuilder(context, StovetimeDatabase::class.java).build()`. The DB is internal, so put this override in `:datasources:local` test-support code if it's needed from another module. Seed progress (finished recipes, XP, streak) through the DAOs, not through the UI. |
| Time | Streaks, "this week" and step timers depend on the clock. Seed data relative to the time the code reads and assert on bounds or fixed inputs, never on an exact device time. Follow `date-helpers`; if the code under test reads the system clock directly, flag it before writing the test. |
| Notifications | Timers may post notifications. Grant with `GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)` so no system dialog appears. |
| WorkManager | If timers or reminders schedule work, `WorkManagerTestInitHelper.initializeTestWorkManager(context)` (`work-testing`) so it doesn't actually run. |

Apply overrides in `@Before` with `loadKoinModules(overrides)` and remove them in `@After` with `unloadKoinModules(overrides)`. Koin is already started by `StovetimeApplication`, and later definitions override earlier ones.

## Test tags & robots

- Every node the test touches gets `Modifier.testTag(<Feature>TestTags.X)`. Constants live in `features/<name>/src/main/.../<Feature>TestTags.kt` (internal object), shared by the composable and the test.
- One robot per screen in `androidTest/.../robots/<Screen>Robot.kt`:

  ```kotlin
  class CookModeRobot(
      private val rule: ComposeTestRule,
  ) {
      fun tapNextStep() = rule.onNodeWithTag(CookModeTestTags.NEXT_STEP_BUTTON).performClick()

      fun tapStartTimer() = rule.onNodeWithTag(CookModeTestTags.START_TIMER_BUTTON).performClick()

      fun assertStepTitle(text: String) = rule.onNodeWithTag(CookModeTestTags.STEP_TITLE).assertTextEquals(text)
  }
  ```

  Robots expose actions and screen-local assertions. Cross-screen assertions live in the test body. Select by tag first, then content description, and never by index.

## Scaffold

```kotlin
class FinishesARecipeE2ETest {
    @get:Rule(order = 0)
    val permissions: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val fakes = TestGraph()

    @Before
    fun setUp() = loadKoinModules(fakes.overrides)

    @After
    fun tearDown() = unloadKoinModules(fakes.overrides)

    @Test
    fun finishing_the_first_recipe_on_the_path_shows_the_xp_earned() {
        // given
        fakes.network.respondWith(recipeLookup = successfulRecipeLookupResponse)
        PathRobot(composeRule).tapFirstNode()

        // when
        CookModeRobot(composeRule).run {
            tapNextStep()
            tapFinish()
        }

        // then
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag(FinishTestTags.XP_EARNED).fetchSemanticsNodes().isNotEmpty()
        }
        FinishRobot(composeRule).assertXpEarnedShown()
    }
}
```

Name tests `<Flow>E2ETest` with snake_case methods and given/when/then comments, as in the unit tests. Assert English text from `strings.xml` via `composeRule.activity.getString(R.string.…)` rather than retyping it.

## Dependencies (once per module)

Compose modules already get `ui-test-junit4` and `ui-test-manifest` from the convention plugin. These are in the catalog and get added for `androidTestImplementation` when first needed (tell the user): `androidx-test-rules` (`GrantPermissionRule`), `koin-test`, `ktor-client-mock`, and `work-testing` if work is scheduled. Add a Gradle Managed Device for headless, reproducible runs:

```kotlin
android {
    testOptions {
        animationsDisabled = true
        managedDevices {
            localDevices {
                create("pixel7Api34") {
                    device = "Pixel 7"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }
}
```

Run with `./gradlew :<module>:pixel7Api34DebugAndroidTest`, or `connectedDebugAndroidTest` on a running emulator or plugged-in device.

## Flows worth covering

- Path → first recipe → cook mode through every step → finish screen with XP; the next node on the path unlocks.
- A step with a timer: start it, it shows on the Timers tab, and it keeps counting while you move on.
- Network error with nothing cached → error state, then retry recovers.
- Offline with a recipe already cached → it opens and can be cooked.
- Me tab shows the XP total and weekly streak seeded in Room.

## Never

- Hit the real TheMealDB API or depend on an exact clock time.
- `Thread.sleep`, `delay` or retry loops. Use `waitUntil { predicate }`.
- Assert on ViewModel internals. E2E tests observe UI and boundaries only.
- Share state across tests via `object` singletons.

Fix flakiness at the source. Don't commit until the suite is green, then run `checks`.
