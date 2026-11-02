---
name: date-helpers
description: Where Stovetime's date and time helpers live and how to add one — the computed Now and Today, kotlin.time/kotlinx-datetime types, the device time zone, calendar weeks for the streak, timer durations, and testing time without injecting a Clock.
when_to_use: Code needs the current time or today, converts timestamps, works out which week a cook falls in or how long the streak is, formats a date, time or timer duration, or checks how old something is; or a new date/time helper is about to be written anywhere.
---

# Skill: date-helpers

## The files

Date and time code lives in three places, by what it needs. Read them before adding a helper and reuse one that exists. A file is created the first time a helper needs it.

| Needs | File |
|---|---|
| The clock or the device time zone (`Now`, `Today`, `DeviceTimeZone`, epoch → local date) | `core/data/.../helpers/DateHelpers.kt` |
| Neither: pure date, week and duration rules that take `now`, `today`, `timeZone` and `Locale` as parameters | `core/common/.../helpers/CalendarRules.kt` and `NumberRules.kt` |
| String resources or Compose (Today/Yesterday, "Cooked 2 days ago", "3-week streak", "12:30" on a timer, "1 h 5 min" on a recipe card) | `presentation/ui/.../helpers/` (`DayText.kt`, `DurationText.kt`, `StreakText.kt`) |

`:presentation:ui` can't see `:core:data`, which is why the pure rules sit in `:core:common`. **Feature modules never format dates, durations or numbers themselves** (no `DateTimeFormatter`, `NumberFormat`, `SimpleDateFormat`, `DateUtils`, `getDisplayName` or `String.format("%02d:%02d")`); they call the `:presentation:ui` helpers.

## Helpers to start with

None exist yet. These are the first ones the app needs; create each in its file above when a step first uses it, then move its row into a "Current helpers" table.

| Helper | File | What it gives |
|---|---|---|
| `Now` | `core:data` | The current `Instant`, read fresh on every access |
| `DeviceTimeZone` | `core:data` | `TimeZone.currentSystemDefault()`, read fresh (the user can travel or change it) |
| `Today` | `core:data` | `Now.toLocalDateTime(DeviceTimeZone).date` |
| `Long.asLocalDate` | `core:data` | Epoch millis → `LocalDate` in the device zone (when was this recipe cooked) |
| `LocalDate.weekStart()` | `core:common` | The Monday of that date's calendar week |
| `weeklyStreak(cookedWeeks, thisWeek)` | `core:common` | Consecutive weeks with at least one completed recipe, ending this week or last week |
| `Duration.timerText()` | `core:common` | `"12:30"`, `"1:05:00"` for a running step timer |
| `dayText(today)`, `durationText()`, `streakText()` | `presentation:ui` | Composable wrappers with string resources |
| `elapsedBetween(thenMillis, nowMillis)` | `core:common` (`ElapsedRules.kt`) | `Elapsed`: just now, minutes, hours or days since a moment |
| `Elapsed.agoText()` | `presentation:ui` (`ElapsedText.kt`) | "just now", "2 hours ago", "3 days ago" from plurals |

Add a row when you add a helper.

## Rules

- **The current time is `Now`.** Never call `Clock.System.now()` elsewhere and never inject a `Clock`.
- **Anything time-dependent is a computed getter** (`val X get() = …`), never `val X = Clock.System.now()`. A plain `val` is evaluated once when the file loads and goes stale: "today" freezes and the streak stops moving.
- **Types:** `kotlin.time.Instant` / `Duration` for moments and spans (timer lengths, cook times), `kotlinx.datetime.LocalDate` / `LocalDateTime` for calendar values. Store moments as epoch milliseconds (`Long`) in Room and models (`completedAt`, `createdAt`, `updatedAt`), calendar days as ISO strings (`"2026-11-02"`), and durations as whole seconds or millis with the unit in the name (`durationSeconds`).
- **Time zone:** the device zone only, `TimeZone.currentSystemDefault()` through `DeviceTimeZone`. A cook's day and week are the ones on their phone.
- **The streak is per calendar week.** A week counts when at least one recipe was completed in it (device zone). Weeks start on Monday; that is one constant in `CalendarRules.kt`, so changing it is a one-line change. Never count the streak in rolling 7-day windows.
- **Timers count with durations, not clock maths.** A running step timer stores its end `Instant` and shows `end - Now`; never decrement a counter every second, which drifts when the app is backgrounded.
- **Text shown to people is locale-aware:** take a `Locale` and use `java.time` patterns through the helpers above, never hand-built strings like `"$day $month"`.
- **Naming:** extension properties read as conversions: `asLocalDate`, `asInstantFromMillis`. Use a function only when it needs parameters.
- **Durations:** use `24.hours` / `Duration` constants, not millisecond arithmetic (`86_400_000`).

## Testing time

Don't freeze or mock the clock. Place data relative to the real `Now`, and assert on bounds:

```kotlin
private var testStartedAt = 0L

@Before
fun setup() {
    testStartedAt = Now.toEpochMilliseconds()
}

// stale: cached 8 days ago
recipeCache(updatedAt = testStartedAt - 8.days.inWholeMilliseconds)

// newly fetched data is stamped no earlier than the test start
assertThat(recipe.lastUpdated).isAtLeast(testStartedAt)
```

Boundaries stay exact: `testStartedAt - 24.hours` is already 24 hours old when the code reads `Now`. Week and streak rules live in `:core:common` and take `today` as a parameter, so test them with fixed dates (a Sunday and the Monday after it, a skipped week, a cook just before midnight) and no clock at all. Pure conversions and formatting take fixed inputs too.

## Reference

None yet. When `DateHelpers.kt` and `CalendarRules.kt` land, list them here with one test that uses relative timestamps.
