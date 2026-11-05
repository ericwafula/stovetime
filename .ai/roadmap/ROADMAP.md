# Product Roadmap — Stovetime

> Living document. Update the status as work lands and log decisions at the bottom.
> Status legend: ⬜ not started · 🟨 in progress · ✅ done
>
> Every item below is a step in the [backlog](backlog/README.md), written as a spec: what the user gets, the acceptance criteria, the edge cases and what's out of scope.
> Screen designs live in Figma (see §11).

---

## 1. Vision

Teach people to cook at home, **one recipe at a time**. Stovetime is a learning path for the kitchen: short units of real recipes, each one teaching a skill, with timers built into every step and a little XP and a weekly streak to keep you coming back.

**One-line pitch:** *"Learn to cook, one dish at a time."*

### Name and store listing
- **Name:** Stovetime (chosen 2026-10-24, see §10).
- **Play Store title** (30 max): `Stovetime: Learn to Cook`
- **Short description** (80 max): *Cook real recipes step by step, with timers built in. Earn XP, keep your streak.*

### Target user (v1)
- A beginner or rusty home cook who wants to cook more, not just watch videos about it
- Cooks a few times a week, often after work, with the phone propped up on the counter
- Gets lost in long recipe pages and isn't sure when something is "done"

### Problems we solve
| Problem | Our answer |
|---|---|
| "I don't know where to start" | A path of short units that build on each other, starting with eggs |
| Long recipe pages, losing your place | Cook mode: one step at a time, big text, screen stays on |
| "How long, and how do I know it's done?" | Every timed step has a timer and says what done looks like ("set at the edges, runny yolk") |
| Cooking once, then giving up | XP, a weekly cook goal and a streak, with no guilt for missing a day |
| Wanting more than the lessons | The Recipes tab: browse thousands of recipes from TheMealDB and cook them the same way |

### Product principles
1. **Cooking first, game second.** XP and streaks reward cooking; they never block it.
2. **One step at a time.** Cook mode shows the step you're on and nothing else.
3. **Every time can be started.** A step that mentions a time has a timer.
4. **Encourage, don't nag.** Weekly goals, not daily ones. Missing a week resets the streak quietly.
5. **The Path works offline.** Lessons are bundled with the app; only the Recipes tab needs the network.

---

## 2. Success metrics

| Metric | v1 target |
|---|---|
| New users who finish their first lesson in the first session | ≥ 50% |
| Users who reach a 2-week streak | ≥ 25% of week-1 actives |
| Lessons finished per weekly active user | ≥ 2 |
| Week-4 retention | ≥ 20% |
| Timers that ring on time | ≥ 99.9% |
| Crash-free sessions | ≥ 99.5% |

---

## 3. Roadmap at a glance

| Phase | Name | Goal | Status |
|---|---|---|---|
| 0 | Foundations | Modules, navigation, storage and the TheMealDB client the features build on | ✅ |
| 1 | The Path | Pick the next lesson, learn it, cook it step by step, earn XP and keep the streak | ✅ |
| 2 | Timers | Timers that keep running and ring with the app closed, and a Timers tab | ✅ |
| 3 | Recipes | Sync TheMealDB to the phone, browse and search it offline, and cook any recipe | ✅ |
| 4 | Me | Progress, history, the weekly goal and settings | ✅ |
| 5 | Later | More courses, badges, reminders, dark mode, widgets | ✅ |

The **Path screen (1.2)** is the screen built live in the workshop.

---

## 4. Phases in detail

### Phase 0 — Foundations
| ID | Item | Status |
|---|---|---|
| 0.1 | Design system: `:presentation:design` with tokens and components from Figma | ✅ |
| 0.2 | Module structure, shared results and the MVI ViewModel base | ✅ |
| 0.3 | Navigation with the floating bottom bar (Path, Recipes, Timers, Me) and edge-to-edge | ✅ |
| 0.4 | Local storage: Room for progress, DataStore for settings | ✅ |
| 0.5 | TheMealDB client | ✅ |

### Phase 1 — The Path (MVP core)
| ID | Item | Status |
|---|---|---|
| 1.1 | Course content: "Home basics", Unit 1 Eggs (5 lessons), bundled as JSON | ✅ |
| 1.2 | Path screen: course chip, streak and XP, unit banner, lesson nodes, "Up next" card | ✅ |
| 1.3 | Lesson intro: photo, time, difficulty, XP, what you'll learn, what you'll need | ✅ |
| 1.4 | Cook mode: one step at a time, step timers, tips, screen stays on | ✅ |
| 1.5 | Progress rules: XP, unlocking, weekly goal and week streak | ✅ |
| 1.6 | Finish screen: XP, cook time, streak, the week's cooks, "How did it turn out?" | ✅ |
| 1.7 | End-to-end journey: Path → intro → cook → finish → next lesson unlocked | ✅ |

### Phase 2 — Timers
| ID | Item | Status |
|---|---|---|
| 2.1 | Timer engine: several timers at once, a foreground service, survives the app being closed | ✅ |
| 2.2 | Step timers run on the engine and keep going when you leave cook mode | ✅ |
| 2.3 | Alarm: rings and vibrates, with Stop and "+1 min", over the lock screen | ✅ |
| 2.4 | Timers tab: running timers and a quick named timer | ✅ |

### Phase 3 — Recipes (TheMealDB)
| ID | Item | Status |
|---|---|---|
| 3.1 | Recipe sync: TheMealDB's catalogue kept on the phone, refreshed in the background | ✅ |
| 3.2 | Browse by category and search, from the phone's copy | ✅ |
| 3.3 | Recipe page: fetched on first open, kept for offline | ⬜ |
| 3.4 | Cook any recipe: steps split from the instructions, times turned into timers, XP | ✅ |
| 3.5 | Offline and sync states: last synced, stale data, retry | ✅ |

### Phase 4 — Me
| ID | Item | Status |
|---|---|---|
| 4.1 | Me screen: total XP, current and best streak, recipes cooked, cook history | ✅ |
| 4.2 | Settings: weekly goal, keep screen on while cooking | ✅ |
| 4.3 | Start over: reset all progress, with a confirmation | ✅ |

### Phase 5 — Later
| ID | Item | Status |
|---|---|---|
| 5.1 | More courses: "Rice & grains", "One-pan dinners", "Kenyan favourites" (pilau, chapati, sukuma wiki), switched from the course chip | ✅ |
| 5.2 | Badges for finishing a unit or a long streak | ✅ |
| 5.3 | A gentle reminder when the week's goal is close | ✅ |
| 5.4 | Dark mode | ✅ |
| 5.5 | Home-screen widget with running timers | ✅ |

---

## 5. Module layout

```
:app                    Koin wiring, navigation host, MainActivity
:features:lessons       Path, lesson intro, cook mode, finish screen
:features:recipes       browse, search, recipe page
:features:timers        Timers tab
:features:me            Me tab and settings
:presentation:design    design system (exists)
:presentation:ui        shared composables, MviViewModel, ViewState, launchers
:core:domain            XP, unlock and streak rules (shared by Path, finish and Me)
:core:data              repositories: courses, progress, recipes, settings; the recipe sync worker
:core:common            DataResult and DataError, dispatchers, pure helpers
:datasources:local      Room (progress, cached recipes), DataStore, bundled course JSON
:datasources:network    TheMealDB over Ktor
:sdk:timer              timer engine: foreground service, alarms, notifications
```

Create a module only when the step that needs it starts. Dependencies point down only; features never depend on each other. The `architecture` skill is the source of truth.

---

## 6. Course content schema

Lessons are written by us and ship as JSON inside the app.

```json
{
  "id": "home-basics",
  "title": "Home basics",
  "units": [
    {
      "id": "eggs",
      "number": 1,
      "title": "Crack it: five ways with eggs",
      "lessons": [
        {
          "id": "jammy-eggs-on-toast",
          "title": "Jammy eggs on toast",
          "minutes": 15,
          "difficulty": "easy",
          "xp": 30,
          "photo": "lesson_jammy_eggs_on_toast",
          "learn": ["Timing a soft yolk to the minute", "Stopping the cook with cold water"],
          "ingredients": ["2 eggs, straight from the fridge", "1 thick slice of sourdough", "Butter, salt, chilli flakes"],
          "steps": [
            { "text": "Bring a small pan of water to the boil" },
            {
              "text": "Lower the eggs into the boiling water with a spoon",
              "detail": "Keep it at a steady boil, not a rolling one, so the shells don't crack against the pan.",
              "timer": { "seconds": 390, "doneness": "for a jammy, set-at-the-edges yolk" },
              "tip": "Cold eggs crack in hot water. Lower them slowly and they'll stay whole."
            }
          ]
        }
      ]
    }
  ]
}
```

Unit 1 lessons, in order: Boiled eggs, Scrambled eggs, Jammy eggs on toast, French omelette, Shakshuka.

---

## 7. Data sources

| Data | Source | Notes |
|---|---|---|
| Courses, units, lessons | Written by us, bundled JSON | Works offline. Times cross-checked against food-safety guidance |
| Recipes tab | [TheMealDB](https://www.themealdb.com/api.php) v1, test key `1` | Read-only, no account. `{"meals": null}` means "nothing found". Synced to the phone (see below) |
| Progress | Room on the phone | Cooked lessons and recipes with date, cook time, rating and XP. No backend, no account |
| Settings | DataStore | Weekly goal, keep screen on |

**Recipe sync (offline-first, one-way).** The phone's database is the source of truth; screens never read the network directly. A background job (WorkManager) syncs the catalogue, every category and the meals in it, on first launch and about daily when there's a connection, with retries and backoff. Full recipe details are fetched the first time a recipe is opened and stored after that, so the free API isn't hit hundreds of times a day. Pull to refresh runs the same sync. Nothing is ever uploaded.

---

## 8. XP and streak rules

| Rule | Value |
|---|---|
| Lesson XP | From the lesson's content (`xp`), e.g. +30 for an easy lesson |
| TheMealDB recipe XP | +10 per cook |
| Cooking the same lesson again | +10 practice XP, it doesn't unlock anything new |
| Unlocking | Lessons unlock in order; the next one opens when the current one is cooked |
| A cook counts when | The cook reaches the finish screen |
| Week | Monday to Sunday, in the phone's own time zone |
| Weekly goal | 4 cooks by default, 1 to 7, set in Me |
| Week streak | Weeks in a row that hit the goal. The current week adds to it as soon as the goal is met; a missed week resets it to 0 |

These rules live in `:core:domain` and are covered by tests with fixed dates.

---

## 9. Risks & mitigations

| Risk | Mitigation |
|---|---|
| Wrong times give undercooked food | Every timed step says what done looks like; safe temperatures for meat in later courses; "times are a guide" wording |
| Timers killed by battery savers | Foreground service with an ongoing notification and exact alarms (2.1); test on Samsung, Xiaomi and Tecno |
| TheMealDB is down or slow | Only the Recipes tab depends on it, and it reads the phone's copy; sync retries in the background |
| TheMealDB instructions don't split cleanly into steps | Split on line breaks, then sentences; fall back to one step with the whole text |
| Streaks feel like pressure | Weekly goals, no daily nagging, quiet resets |
| Too much game, not enough cooking | XP only for cooking; nothing is locked behind XP |

---

## 10. Decision log

| Date | Decision | Why |
|---|---|---|
| 2026-10-24 | Name: **Stovetime** | Short, about cooking at home |
| 2026-10-24 | Offline-first, no account or backend in v1 | Kitchen use, privacy, a smaller first release |
| 2026-10-24 | Public repo; `.ai/` is committed | Workshop attendees can copy the whole agent setup |
| 2026-10-31 | A learning path for home cooks, gamified like Duolingo | Replaces the timers-and-guides app; the path gives beginners a place to start and a reason to come back |
| 2026-10-31 | A custom design system, no Material components | The "tactile flat" look in Figma: chunky buttons with a ledge, warm palette, Gabarito and Rubik |
| 2026-11-02 | Path lessons are curated and bundled; TheMealDB powers the Recipes tab | The designs need teaching content (what you'll learn, step timers, tips) that the API doesn't have |
| 2026-11-02 | Streak = weeks in a row hitting a weekly cook goal (default 4) | Matches the finish screen; weekly is kinder than daily |
| 2026-11-02 | The Timers tab shows running timers and quick timers | Cooking often has two things on at once |
| 2026-11-02 | Weeks start on Monday, device time zone | Simple and predictable; one constant if it ever changes |
| 2026-11-02 | Frames added for Recipes, Timers, the quick timer sheet, the alarm and Me; old v1–v3 designs removed from Figma | Every UI step has a frame to build from |
| 2026-11-02 | The timers widget is a plain `RemoteViews` widget in `:app` (three fixed rows, a native count-down `Chronometer`), refreshed from the engine's timers on the application scope | Glance has no chronometer; a native one counts down on the home screen without waking the app every second |
| 2026-11-02 | Badges are worked out from progress (unit badges from cooked lessons, streak badges at 2, 4 and 8 weeks from the best week streak), never stored | Start over removes them for free and they can never disagree with the history |
| 2026-11-02 | The finish screen compares the badges earned before saving the cook with those after, and celebrates only the new ones | A badge earned on an earlier cook isn't celebrated again |
| 2026-11-02 | The Path keeps its course chip, streak and XP pinned above the scrolling path, and scrolls the "Up next" card clear of the floating bar | Deep in a course the next lesson's Start sat under the nav bar; the stats stay in view while it scrolls |
| 2026-11-02 | Phase 5 becomes five steps (5.1–5.5) in `backlog/phase-5-later.md` now that Phases 0–4 have shipped | The roadmap held Phase 5 at roadmap level only until 0–4 shipped; each idea gets acceptance criteria before it is built |
| 2026-11-02 | Recipes are synced offline-first: Room is the source of truth, WorkManager pulls the catalogue daily, details are fetched on open | Browse and search work offline; the free API isn't hammered; the same pattern Fumi uses |
| 2026-11-02 | Each cook stores the weekly goal that was set when it was cooked | A past week is judged by the goal it had, so changing the goal never rewrites past weeks; the current week uses today's goal |
| 2026-11-02 | The best streak counts runs of finished weeks that met their goal, plus this week once it's met | Me shows it next to the current streak, and it survives a missed week |
| 2026-11-02 | The Path feature module is `:features:lessons`, not `:features:path` | Gradle's type-safe project accessors can't generate `projects.features.path` (it clashes with `ProjectDependency.getPath()`) |
| 2026-11-02 | A catalogue meal's sync stamp is the time a sync added or last changed it; "newest sync first" sorts on it | Unchanged meals aren't rewritten on every sync, and newly added meals surface at the top of All |
| 2026-11-02 | Recipe sync runs as two unique works (daily periodic, and a one-time run for first launch, pull to refresh and retry) behind one in-process lock | WorkManager can't make a periodic and a one-time request share a unique name; the lock makes a second request wait for the running sync instead of starting another |
| 2026-11-02 | Catalogue search filters the synced meals in memory in the repository, folding case and accents with `java.text.Normalizer` | About 300 meals; keeps the rule in one tested place and needs no extra column |
| 2026-11-02 | A recipe's full details are fetched once on first open and not refreshed after that | TheMealDB recipes rarely change and the page must open offline; a later step can add a refresh if needed |
| 2026-11-02 | The time of the last failed recipe sync is stored next to the last successful one | 3.5 shows a note only when syncing keeps failing for over a week |
| 2026-11-02 | Pull to refresh on the Recipes tab uses the `pullToRefresh` modifier from material3 with a design-system `Pill` as the indicator | Compose foundation has no pull gesture; only the gesture comes from material3, nothing Material is drawn |
| 2026-11-02 | `StoveChip`, `StoveSearchField` and `StovePhotoCard` join `:presentation:design`, built from the Recipes — Browse frame | They hold the frame's sizes and borders, keeping dp literals out of the feature; Path and Me can reuse the card |
| 2026-11-02 | The Recipes tab's loading, empty and no-match states are plain centred text (and a Try again button) until Figma has frames for them | The Browse frame shows only the filled state |
| 2026-11-02 | Images load with Coil 3.3 over Ktor through one `RemotePhoto` in `:presentation:ui`, falling back to the sunken colour | Ktor is already the app's HTTP client; one shared composable keeps placeholders consistent |
| 2026-11-02 | The recipe page leaves out the lesson-only parts of the Recipe — Intro frame (unit label, time, difficulty, what you'll learn) and shows category, area and +10 XP pills; a ticked ingredient is a basil check | TheMealDB has no times, levels or skills; the frame has no ticked state |
| 2026-11-02 | `StoveIconButton` and `StoveCheckRow` join `:presentation:design` | The close button and the ingredient checklist are shared with the lesson intro (1.3) and need 48 dp touch targets |
| 2026-11-02 | Start cooking on the recipe page sends its event but `StovetimeNavHost` wires it to an empty lambda until cook mode exists | 3.4 builds cook mode for recipes on top of 1.4 |
| 2026-11-02 | Offline, the Recipes header line becomes "Offline · last synced …"; a sync that has kept failing for over a week shows a yolk note with Try again | The note stays small and only appears when the copy is really old |
| 2026-11-02 | Coming back online starts a sync from the Recipes tab when the last one is over a day old; elsewhere the daily periodic work catches up once a connection returns | Keeps the app start-up free of coroutines; WorkManager already runs an overdue periodic sync when its network constraint is met |
| 2026-11-02 | When the phone blocks full-screen alarms (Android 14+), the first timer explains it once and offers the system setting, next to the one-time exact-alarm note | A timer app outside the alarm category doesn't get full-screen intents by default; without them the alarm is only a heads-up over the lock screen |
| 2026-11-02 | The alarm is `AlarmActivity` in `:app` (single instance, own task, excluded from recents); a new alarm for another timer switches it to that timer, and stopping or snoozing the shown one moves on to any other timer still ringing before it closes | One alarm screen over the lock screen at a time; each timer keeps its own alarm notification, so none is lost |
| 2026-11-02 | The alarm's detail line is "Take them out now" + the step's doneness; a timer with no doneness (a quick timer) shows only its name | Matches the frame's copy for a step timer without inventing text for quick timers |
| 2026-11-02 | `StoveIconDisc` joins `:presentation:design` for the alarm's white disc with a ledge | Keeps the ledge in `LedgeSurface` instead of hand-rolled offsets in the screen |
| 2026-11-02 | The Timers list puts ringing timers first, then running ones soonest-ending first, then paused ones; the header counts every timer ("2 running" includes paused ones, as in the frame) | Matches the frame and the engine's own order |
| 2026-11-02 | A ringing timer's card shows "Time's up" with Stop and "+1 min" in place of Pause; tapping a card opens cook mode only for a step timer | The frame has no ringing card; these are the alarm's own actions |
| 2026-11-02 | The quick timer sheet sets seconds with a second row of "−10 s" / "+10 s" pills under the frame's "−1 min / 5:00 / +1 min" row; it opens at 5:00 and stays between 10 seconds and 3 hours | The frame shows minutes only and the spec asks for minutes and seconds; pills fit the frame better than a tap-to-edit field |
| 2026-11-02 | An unnamed quick timer takes "Timer N" with the lowest N that no current timer is named; the number comes from a localised "Timer %d" the screen passes in | Numbers are reused once a timer ends, so they stay small |
| 2026-11-02 | `StoveTextField` (with `StoveSearchField` built on it) and `StoveStepButton` join `:presentation:design` | The quick timer sheet's name field and −/+ pills, with 48 dp touch targets |
| 2026-11-02 | Me's subline is the month of the first cook ("Home cook since October", with the year once it isn't this year); before any cook it reads "Ready for your first cook" | The frame only shows the filled line; the month comes from the oldest cook in the history |
| 2026-11-02 | The best streak shows as a caption under the three stat tiles ("Best streak: 5 weeks") | The Me frame has no slot for it and a fourth tile doesn't fit a 360 dp screen |
| 2026-11-02 | History dates read Today, Yesterday, the weekday for earlier days of this calendar week, and a short date before that | Matches the frame ("Today", "Wednesday") and the week row above it; `relativeDay` in `:core:common` holds the rule |
| 2026-11-02 | `StoveCard` takes a `shape`; Me's history and settings cards use the medium radius with the usual ledge | The frame's cards are 16 dp; history items are pressable, so they keep the ledge |
| 2026-11-02 | Empty Me history is a card with "Go to the Path"; while progress loads nothing is drawn, and a failed load shows Try again | The frame shows only the filled state; same pattern as the Path tab |
| 2026-11-02 | `StoveSwitch` and `StoveStepper` join `:presentation:design` (with a new `Minus` icon); the stepper's 36 dp buttons sit in 48 dp touch targets and dim at 1 and 7 | The Me frame needs both and there was no switch or stepper; no Material components |
| 2026-11-02 | A setting changed in Me shows straight away and is saved in the background; if saving fails it goes back and a toast says so | DataStore is fast, so the stepper shouldn't wait on it; the Path, finish screen and Me all read the goal through `observeProgress` |
| 2026-11-02 | Start over confirms in the shared `ConfirmLauncher` sheet ("Delete my progress" / "Keep my progress") and deletes only the cooks table; the Path and Me recompute from the empty history | Settings live in DataStore and recipes in their own tables, so both survive; the frame shows only the link |
| 2026-11-02 | `StoveTextButton` joins `:presentation:design` for the red "Start over" link | A text-only action with a 48 dp touch target, keeping sizes out of the feature |
| 2026-11-02 | The weekly reminder rule is a pure function in `:core:domain` (`weeklyReminder` in `ReminderRules.kt`) behind `CheckWeeklyReminderUseCase`, fed by `ObserveProgressUseCase` | The week, goal and streak rules already live there; the reminder reuses them instead of counting again |
| 2026-11-02 | `WeeklyReminderWorker`, its schedule and the reminder notification live in `:app` (`reminders/`), not `:core:data` | The worker needs a `:core:domain` use case, which `:core:data` can't see, and the notification opens `MainActivity`; the worker itself holds no rules |
| 2026-11-02 | The reminder check is unique daily periodic work (`KEEP`) first set for 18:00 local; it posts only on Saturday from 17:00 when 1 or 2 cooks are left, the switch is on and this week wasn't reminded yet | Once a day keeps it gentle; an hour of slack past 17:00 lets WorkManager run late and still land on Saturday evening |
| 2026-11-02 | The week of the last reminder is saved (as the week's Monday) in its own `weekly_reminder` DataStore file before the notification is posted | Saving first means a retry after a failed save can never post twice in a week |
| 2026-11-02 | The reminder has its own low-importance "Weekly reminder" channel and only posts when notifications are allowed; nothing asks for the permission on its behalf | Encourage, don't nag; timers already ask for the permission when it matters |
| 2026-11-02 | Tapping the reminder opens `MainActivity` with an "open Path" extra, which selects the Path tab even when the app is already open on another tab | Launching the activity plainly would leave a running app on whatever tab it was on |
| 2026-11-02 | Me's settings card gets a "Weekly reminder" switch under "Keep screen on", on by default, saved with the other cook settings | Same save-then-revert pattern as the other settings |
| 2026-11-02 | Dark mode follows the phone: `DarkColors` uses warm espresso surfaces: canvas Roast `#211A14`, surface Walnut `#2E251D`, sunken Char `#17120E`, border and ledge Bark `#4E4034` — with text Oat `#F2EADB` and Biscuit `#A6937C`, locked Clay `#3A3028` / Taupe `#7D6E5E`, and a black 60% scrim | Figma has light frames only; not pure black so the flat look stays warm, sunken stays darker than canvas so recesses read, and Bark keeps ledges and borders visible (1.7:1 on canvas) |
| 2026-11-02 | The tomato, basil and yolk faces and ledges are the same in both themes; the soft fills darken to Tomato900 `#4A1F21`, Basil900 `#1F3A27`, Yolk900 `#3F3115` | Accents stay recognisable and white text stays on tomato and basil; pale soft fills would glare on dark |
| 2026-11-02 | New tokens `primaryContent`, `successContent`, `xpContent` (accent text and icons on plain or soft backgrounds; dark Tomato300 `#FF8A82`, Basil300 `#7DC98E`, Yolk300 `#F7C75A`, light unchanged), `onXp` (Espresso text on the yolk banner) and `scrim` | `*Shade` was both a ledge and a text colour; a ledge must be darker than its face but text on a dark soft fill must be light, so the two meanings split |
| 2026-11-02 | Light Tomato500 `#E2333D` → `#E12B36`, Basil500 `#3F9A55` → `#36854A` (Basil700 `#2C7440` → `#256135` to keep the ledge step) and Cocoa `#7A6857` → `#786655` | White on tomato was 4.40:1, white on basil 3.52:1 and Cocoa on Oat 4.45:1; each now passes 4.5:1, and `StovetimeColorsTest` keeps every text pair at 4.5:1 in both themes |
| 2026-11-02 | The window theme is `Theme.Stovetime` (`values` / `values-night`) with `stovetime_canvas` as its background; `MainActivity` handles `uiMode` and re-applies `SystemBarStyle.auto` and the background on a theme switch; the alarm is forced light | No white flash on a dark launch and no recreation (state kept) when the phone switches theme; the alarm stays red with white text and light bar icons |
| 2026-11-02 | Each course is its own JSON file under `assets/courses/`, loaded in a fixed order from one list with Home basics first; a test fails if a file there isn't in the list | Adding a course is one file and one line, and the order on the sheet doesn't depend on file names |
| 2026-11-02 | Lesson ids are unique across every course, and a test checks it | A cook stores only the lesson id, so unlocking stays per course and the intro, cook mode and Me history can find a lesson in any course without a course id on the cook |
| 2026-11-02 | The chosen course id lives in its own DataStore file (`course_choice`); a missing or unknown id falls back to Home basics | Survives restarts, and a course removed in a later release can't leave the Path empty |
| 2026-11-02 | The lesson intro looks the lesson up in every course and shows its position in that course's own unit | Me history can open a lesson from a course that isn't on the Path right now |
| 2026-11-02 | The course sheet shows one card per course (title and "N of M cooked"), the current one in basil with a check; N counts different lessons cooked, so cooking one again doesn't raise it | Figma only has the sheet itself (from the quick timer), not its rows; the cards reuse `StoveCard` and the selected look of `StoveChoice` |
| 2026-11-02 | The course chip sits in a 48 dp tap area and reads "Change course" to screen readers | The chip itself is shorter than a touch target and the frame keeps its size |
| 2026-11-02 | The new courses name their photos (`lesson_<id>`) but don't bundle them yet; only Home basics is checked for bundled photos | Photography is out of scope for 5.1, and the Path, intro and finish screens already fall back to a plain placeholder |
| 2026-11-05 | The recipe page gets its own Figma frame, Recipe — Details (`106:103`): photo, name, category, area and +10 XP pills, ingredients with measures, Start cooking | The workshop builds 3.3 from Figma, and the lesson intro frame showed parts a TheMealDB recipe doesn't have |

---

## 11. Design

Screens are in Figma: **[Stovetime](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN)**, page **"v4 — Home cook"**. Read them with the Figma MCP (`implement-design` skill), never from screenshots.

| Frame | Node | Step |
|---|---|---|
| Home — Path | [`70:98`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=70-98) | 1.2, nav bar for 0.3 |
| Recipe — Intro | [`72:120`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-120) | 1.3 |
| Recipe — Details | [`106:103`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=106-103) | 3.3 |
| Cook — Step | [`72:158`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-158) | 1.4 |
| Cook — Done | [`72:184`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-184) | 1.6 |
| Recipes — Browse | [`80:48`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=80-48) | 3.2 |
| Timers | [`80:110`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=80-110) | 2.4 |
| Timers — Quick timer | [`81:194`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-194) | 2.4 |
| Timer — Alarm | [`81:178`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-178) | 2.3 |
| Me | [`81:73`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-73) | 4.1, 4.2, 4.3 |
| Photos (lesson and sample dish photos) | [`79:141`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=79-141) | 1.1, 3.2 |
| Components (Button, PathNode, StatChip, TimerChip, Progress) and the Home palette, theme and size variables | — | 0.1 |

The recipe page (3.3) has its own frame, Recipe — Details, built from the Recipe — Intro layout. The lesson photos are exported to `.ai/roadmap/content/photos/`.

**The look: tactile flat.** Flat colour with no gradients or shadows, but every pressable thing sits on a darker "ledge" and drops into it when pressed. Primary is tomato (`#E12B36`); success is basil green; XP is yolk yellow. Dark mode keeps the same accents on warm espresso surfaces and follows the phone. Type is Gabarito for headings and numbers, Rubik for text. The food photos are AI-generated placeholders; replace them with real photography before launch.
