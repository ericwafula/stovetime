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
| 0 | Foundations | Modules, navigation, storage and the TheMealDB client the features build on | 🟨 |
| 1 | The Path | Pick the next lesson, learn it, cook it step by step, earn XP and keep the streak | ⬜ |
| 2 | Timers | Timers that keep running and ring with the app closed, and a Timers tab | ⬜ |
| 3 | Recipes | Sync TheMealDB to the phone, browse and search it offline, and cook any recipe | ⬜ |
| 4 | Me | Progress, history, the weekly goal and settings | ⬜ |
| 5 | Later | More courses, badges, reminders, dark mode, widgets | ⬜ |

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
| 1.6 | Finish screen: XP, cook time, streak, the week's cooks, "How did it turn out?" | ⬜ |
| 1.7 | End-to-end journey: Path → intro → cook → finish → next lesson unlocked | ⬜ |

### Phase 2 — Timers
| ID | Item | Status |
|---|---|---|
| 2.1 | Timer engine: several timers at once, a foreground service, survives the app being closed | ⬜ |
| 2.2 | Step timers run on the engine and keep going when you leave cook mode | ✅ |
| 2.3 | Alarm: rings and vibrates, with Stop and "+1 min", over the lock screen | ⬜ |
| 2.4 | Timers tab: running timers and a quick named timer | ⬜ |

### Phase 3 — Recipes (TheMealDB)
| ID | Item | Status |
|---|---|---|
| 3.1 | Recipe sync: TheMealDB's catalogue kept on the phone, refreshed in the background | ⬜ |
| 3.2 | Browse by category and search, from the phone's copy | ⬜ |
| 3.3 | Recipe page: fetched on first open, kept for offline | ⬜ |
| 3.4 | Cook any recipe: steps split from the instructions, times turned into timers, XP | ⬜ |
| 3.5 | Offline and sync states: last synced, stale data, retry | ⬜ |

### Phase 4 — Me
| ID | Item | Status |
|---|---|---|
| 4.1 | Me screen: total XP, current and best streak, recipes cooked, cook history | ⬜ |
| 4.2 | Settings: weekly goal, keep screen on while cooking | ⬜ |
| 4.3 | Start over: reset all progress, with a confirmation | ⬜ |

### Phase 5 — Later (evidence-driven)
- More courses: "Rice & grains", "One-pan dinners", "Kenyan favourites" (pilau, chapati, sukuma wiki)
- Badges for finishing a unit or a long streak
- A gentle reminder when the week's goal is close
- Dark mode
- Home-screen widget with running timers

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
| 2026-11-02 | Recipes are synced offline-first: Room is the source of truth, WorkManager pulls the catalogue daily, details are fetched on open | Browse and search work offline; the free API isn't hammered; the same pattern Fumi uses |
| 2026-11-02 | Each cook stores the weekly goal that was set when it was cooked | A past week is judged by the goal it had, so changing the goal never rewrites past weeks; the current week uses today's goal |
| 2026-11-02 | The best streak counts runs of finished weeks that met their goal, plus this week once it's met | Me shows it next to the current streak, and it survives a missed week |
| 2026-11-02 | The Path feature module is `:features:lessons`, not `:features:path` | Gradle's type-safe project accessors can't generate `projects.features.path` (it clashes with `ProjectDependency.getPath()`) |

---

## 11. Design

Screens are in Figma: **[Stovetime](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN)**, page **"v4 — Home cook"**. Read them with the Figma MCP (`implement-design` skill), never from screenshots.

| Frame | Node | Step |
|---|---|---|
| Home — Path | [`70:98`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=70-98) | 1.2, nav bar for 0.3 |
| Recipe — Intro | [`72:120`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-120) | 1.3 |
| Cook — Step | [`72:158`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-158) | 1.4 |
| Cook — Done | [`72:184`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-184) | 1.6 |
| Recipes — Browse | [`80:48`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=80-48) | 3.2 |
| Timers | [`80:110`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=80-110) | 2.4 |
| Timers — Quick timer | [`81:194`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-194) | 2.4 |
| Timer — Alarm | [`81:178`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-178) | 2.3 |
| Me | [`81:73`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-73) | 4.1, 4.2, 4.3 |
| Photos (lesson and sample dish photos) | [`79:141`](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=79-141) | 1.1, 3.2 |
| Components (Button, PathNode, StatChip, TimerChip, Progress) and the Home palette, theme and size variables | — | 0.1 |

The recipe page (3.3) reuses the Recipe — Intro layout. The lesson photos are exported to `.ai/roadmap/content/photos/`.

**The look: tactile flat.** Flat colour with no gradients or shadows, but every pressable thing sits on a darker "ledge" and drops into it when pressed. Primary is tomato (`#E2333D`); success is basil green; XP is yolk yellow. Type is Gabarito for headings and numbers, Rubik for text. The food photos are AI-generated placeholders; replace them with real photography before launch.
