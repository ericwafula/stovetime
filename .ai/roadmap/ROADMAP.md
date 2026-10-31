# Product Roadmap — Stovetime

> Living document. Update checkboxes as work lands and log decisions at the bottom.
> Status legend: ⬜ not started · 🟨 in progress · ✅ done
>
> Each item below is broken into a step with a description and acceptance criteria in the [backlog](backlog/README.md).
> Screen designs live in Figma (see §11).

---

## 1. Vision

Help home cooks **get the timing right**: know how long to cook something, follow each step at the right moment, and never burn the chicken while the rice boils over. Recipes, a shopping list and a personal cookbook grow around that core.

**One-line pitch:** *"Know exactly when to flip, stir and rest."*

### Name and store listing
- **Name:** Stovetime. Chosen 2026-10-24, see §10.
- **Play Store title** (30 max): `Stovetime: Recipes & Timers`
- **Short description** (80 max): *Step-by-step recipes with built-in timers. Know when to flip, stir and rest.*
- **Why:** "How long to boil eggs" is one of the most-searched cooking questions on Google, and "how to cook rice" and chicken cooking times rank high too. Recipe apps compete on "recipes", "meal planner" and "shopping list"; timer apps on "cooking timer" and "kitchen timer". Stovetime sits where both meet: cooking times as a first-class feature inside a recipe app.
- **Before launch:** search the Play Store and App Store for exact and similar names, check domains (stovetime.app / .com) and trademarks in software classes. Similar existing names to avoid confusion with: Simmer, Cook:Clock, Thyme.

### Target user (v1)
- Home cook who cooks most days, often two or three things at once
- Uses the phone in the kitchen with wet or messy hands
- Already looks up "how long to cook X" online, or keeps recipes in screenshots and browser tabs

### Problems we solve
| Problem | Our answer |
|---|---|
| "How long do I boil eggs / cook chicken / rest beef?" | Cooking guides with tested times and doneness choices |
| Juggling several things on the stove at once | Multiple labelled timers running together, with alarms that work in the background |
| Losing your place in a recipe and missing a timed step | Cook mode: one step at a time, screen stays on, tap a time in the step to start its timer |
| Forgetting an ingredient at the shop | Shopping list built from recipes, merged and grouped by aisle |
| Recipes scattered across screenshots and tabs | A personal cookbook with collections, notes and a record of what you've cooked |

### Product principles
1. **Timing first** — every screen that mentions a time lets you start it.
2. **Messy-hands friendly** — big targets, few taps, the screen stays awake while cooking.
3. **Offline-first** — everything works with no connection; there is no account and no backend in v1.
4. **Honest guidance** — times are guides; always show the doneness check ("juices run clear", "75 °C inside").
5. **Small and fast** — small APK, starts quickly on a low-end phone.

---

## 2. Success metrics

| Metric | v1 target |
|---|---|
| New users who start a timer in their first session | ≥ 60% |
| Users who run two or more timers at once at least once a week | ≥ 25% of weekly actives |
| Users who save at least one recipe in the first week | ≥ 30% |
| Week-4 retention | ≥ 20% |
| Timers that ring on time (no missed alarms) | ≥ 99.9% |
| Crash-free sessions | ≥ 99.5% |

---

## 3. Roadmap at a glance

| Phase | Name | Goal | Status |
|---|---|---|---|
| 0 | Foundations | Modules, navigation, design system, local storage, a timer engine that survives the background | ⬜ |
| 1 | Timers & cooking guides | "How long do I cook this?" answered and timed, for eggs, chicken, beef and rice first | ⬜ |
| 2 | Recipes & cook mode | Add a recipe and cook it step by step with tap-to-start timers | ⬜ |
| 3 | Shopping list | Turn recipes into one merged list and tick it off at the shop | ⬜ |
| 4 | My cookbook | Collections, notes, what you've cooked, import from the web, share | ⬜ |
| 5 | Later | Meal planner, finish-together scheduler, hands-free, widgets, Wear OS | ⬜ |

---

## 4. Phases in detail

### Phase 0 — Foundations
| ID | Item | Status |
|---|---|---|
| 0.1 | Module structure and navigation with a bottom bar (Timers, Recipes, Shopping, Cookbook) | ⬜ |
| 0.2 | Design system module from the Figma file | ⬜ |
| 0.3 | Local database (Room) and saved preferences | ⬜ |
| 0.4 | Timer engine: foreground service, alarms, notifications that survive the app being closed | ⬜ |
| 0.5 | CI quality checks and a public README | ⬜ |

### Phase 1 — Timers & cooking guides (MVP core)
| ID | Item | Status |
|---|---|---|
| 1.1 | Quick timer: set a time, give it a name, start | ⬜ |
| 1.2 | Several timers at once, each labelled, with one ongoing notification | ⬜ |
| 1.3 | Cooking guides content: eggs, chicken, beef, rice (times, methods, doneness checks) | ⬜ |
| 1.4 | Boiled eggs guide: soft, jammy, hard → one tap starts the timer | ⬜ |
| 1.5 | Chicken and beef guides: by cut, method and doneness, as timed step sequences | ⬜ |
| 1.6 | Step sequences: a timer that moves to the next step ("flip", "rest") and alerts at each | ⬜ |
| 1.7 | Alarm screen over the lock screen, with snooze "+1 min" and stop | ⬜ |
| 1.8 | Alarms that always ring: exact alarms, full-screen fallback, silent mode, budget-phone battery savers, Live Updates | ⬜ |
| 1.9 | Timer states and first run: empty rail, paused, overrun, permissions off | ⬜ |
| 1.10 | Kitchen-ready accessibility: 200% font scale, TalkBack cadence, screen on, landscape | ⬜ |

### Phase 2 — Recipes & cook mode
| ID | Item | Status |
|---|---|---|
| 2.1 | Recipe model and storage (ingredients, steps, servings, times, photo) | ⬜ |
| 2.2 | Add and edit a recipe by hand | ⬜ |
| 2.3 | Recipe screen with servings scaling and unit conversion | ⬜ |
| 2.4 | Cook mode: one step at a time, screen stays on, big swipe targets | ⬜ |
| 2.5 | Times inside steps become tap-to-start timers ("simmer for 20 minutes") | ⬜ |
| 2.6 | Starter recipes bundled with the app | ⬜ |

### Phase 3 — Shopping list
| ID | Item | Status |
|---|---|---|
| 3.1 | Add a recipe's ingredients to the list (choose which) | ⬜ |
| 3.2 | Merge duplicates and group by aisle | ⬜ |
| 3.3 | Tick off, add your own items, clear done items | ⬜ |
| 3.4 | Share the list as text | ⬜ |

### Phase 4 — My cookbook
| ID | Item | Status |
|---|---|---|
| 4.1 | Favourites and collections ("Weeknight", "Sunday lunch") | ⬜ |
| 4.2 | "I cooked this": cook history, personal notes and a rating | ⬜ |
| 4.3 | Import a recipe from a web link (schema.org Recipe data) | ⬜ |
| 4.4 | Share a recipe as a card or text | ⬜ |
| 4.5 | Cookbook home: recently cooked, most cooked, never tried | ⬜ |

### Phase 5 — Later (evidence-driven)
- Weekly meal planner that feeds the shopping list
- Finish-together scheduler: pick several dishes, the app tells you when to start each so they're ready at the same time
- Hands-free cook mode (voice "next step", "start timer")
- Home-screen widget with running timers; Wear OS timers
- Cook from what you have (pantry suggestions)

---

## 5. Proposed module layout

```
:app
:core:common          shared types, dispatchers, results
:core:domain          use cases (only when logic is shared)
:core:data            repositories
:datasources:local    Room database, DataStore
:presentation:ui      shared composables and launchers
:presentation:design  design system (tokens + components from Figma)
:sdk:timer            timer engine: foreground service, alarms, notifications
:features:timers      quick timers, running timers, alarm screen
:features:guides      cooking guides (eggs, chicken, beef, rice …)
:features:recipes     recipe list, detail, editor, cook mode
:features:shopping    shopping list
:features:cookbook    collections, history, import, share
:features:settings    settings
```

Build logic and conventions come from Sowcast's `build-logic` (`stovetime.*` convention plugins).

---

## 6. Cooking guide schema (draft)

```yaml
id: boiled-eggs
title: Boiled eggs
category: eggs
options:                     # what the cook chooses
  - id: soft
    label: Soft (runny yolk)
    steps:
      - text: Lower eggs into boiling water
      - text: Boil
        duration: 6m
      - text: Move to cold water
        duration: 2m
    doneness: White set, yolk runny
  - id: hard
    label: Hard
    steps:
      - text: Boil
        duration: 11m
    doneness: Yolk pale and firm all the way through
notes: Times are for large eggs straight from the fridge. Add 1 minute for extra-large.
```

Guides ship as JSON in the app (no network). Every time is a guide; every guide states its doneness check.

---

## 7. Data sources

| Data | Source | Notes |
|---|---|---|
| Cooking guides | Written by us, bundled in the app | Cross-checked against food-safety temperatures (e.g. chicken 74–75 °C inside) |
| Starter recipes | Written by us, bundled | Include a few Kenyan favourites (pilau, chapati, sukuma wiki) |
| Imported recipes | schema.org `Recipe` data on the web page the user shares | Phase 4; parsed on the device |

---

## 8. Risks & mitigations

| Risk | Mitigation |
|---|---|
| Timers killed by battery savers on some phones | Foreground service with an ongoing notification; alarms via AlarmManager; test on Samsung, Xiaomi, Tecno |
| Wrong cooking times cause undercooked food | Doneness checks on every guide; safe internal temperatures; "times are a guide" wording |
| Too many features too early | Phases 0–1 are a complete, useful timer app on their own |
| Recipe import breaks on some sites | Only sites with schema.org Recipe data; clear message when a page has none |

---

## 9. Open questions

- English only for v1, or English and Swahili from the start?
- Celsius only, or a °C / °F setting from day one?
- Should cooking guides be editable by the user ("my eggs take 7 minutes")?

---

## 10. Decision log

| Date | Decision | Why |
|---|---|---|
| 2026-10-24 | Name: **Stovetime**, store title "Stovetime: Recipes & Timers" | Timing is the hero feature; "how long to cook X" questions are among the most-searched cooking queries; Simmer and Cook:Clock were taken |
| 2026-10-24 | Offline-first, no account or backend in v1 | Kitchen use, privacy, and a smaller first release |
| 2026-10-24 | Public repo; `.ai/` and `.claude/` are committed | Workshop attendees can copy the whole agent setup (unlike Sowcast, where they stay local) |
| 2026-10-24 | Build logic reused from Sowcast | Same toolchain and conventions; no time spent on setup |

---

## 11. Design

Screens are designed in Figma: **[Stovetime — Phase 1 timers](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN)** (education team). Each UI step in the backlog carries a **Design:** line pointing at its frame.

| Frame | Step |
|---|---|
| Components v3 (status bar, primary and secondary buttons, category, nav bar, Ticket with Rail / Order / Step variants) + "Stovetime palette" and "Stovetime theme" variables | 0.2 |
| 1.1 Quick timer — new ticket | 1.1 |
| 1.2 Timers — on the rail | 1.2 |
| 1.4 Boiled eggs guide | 1.4 |
| 1.5 Chicken guide | 1.5 |
| 1.6 Step sequence — cook mode | 1.6 |
| 1.7 Alarm — full screen, 1.7b Ongoing notification | 1.7 |
| 1.9a Rail — empty, 1.9b Paused, overrun, notifications off, 1.9c First run | 1.9 |

The current designs are on the **"v3 — Kitchen ticket"** page, in a Light row and a Dark row. The layout borrows from delivery apps people already know: Uber Eats for the category row, photo cards and store-style guide pages; Glovo and talabat for the cook-mode tracker (big countdown, "On track" pill, step tracker). The kitchen feel comes from the pass: running timers hang as order tickets on a rail, tickets use a receipt font (Courier Prime) with a torn edge, and "Fire" starts a timer. The food photos are AI-generated in Figma (6 images); replace them with real photography before launch. Earlier passes stay for comparison: "v2 — Kitchen Stories style" and "Page 1".

**Android first.** Material status bar and gesture handle, back arrows instead of iOS close buttons, and 48dp touch targets (Compose's `minimumInteractiveComponentSize` covers controls drawn smaller). A finished timer uses a full-screen intent (1.7); a running one is an ongoing notification with segmented progress and a status-bar chip (1.7b, Android 16 Live Updates).

**Colour.** Two layers of variables. "Stovetime palette" holds the raw colours (neutral, tomato, herb, butter, paper, red, plus alpha values) and is hidden from pickers. "Stovetime theme" holds Material 3 roles with Light and Dark modes that point at the palette: `surface`, `onSurface`, `onSurfaceVariant`, `surfaceContainerHigh(est)`, `outlineVariant`, `primary`/`onPrimary` (near-black actions), `tertiary` (tomato: time and urgency), `tertiaryContainer`, `error`, and extended roles for what Material doesn't name (`success`, `highlightContainer`, `ticket`, `ticketContainer`, `ticketAccent`, `heroSurface`, `onHero`, `heroScrim`). Every layer is bound to a role, never to a palette colour, so this maps one-to-one onto `MaterialTheme.colorScheme` plus a small `StovetimeColors` for the extended roles. Translucent colours are alpha tokens, not layer opacity. Every text pair meets WCAG AA (4.5:1, or 3:1 for large text) in both modes; small text over photos sits on a 70% scrim and uses `onHero`.
