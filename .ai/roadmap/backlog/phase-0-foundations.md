# Phase 0 — Foundations

**Order:** 0.2 → 0.1 → 0.3 → 0.4 → 0.5

## 0.1 Navigation and bottom bar

**Size:** M · **Depends on:** 0.2

### Description
Give the app its four main areas: Timers, Recipes, Shopping and Cookbook, reachable from a bottom bar. Each tab is a placeholder screen for now.

### Acceptance Criteria
- [ ] The app opens on Timers
- [ ] Tapping each tab opens its screen, and the selected tab is highlighted
- [ ] Back from any tab returns to Timers; back on Timers closes the app
- [ ] Rotation and returning from the background keep the user on the same tab
- [ ] Routes are type-safe and defined in one place; feature modules don't depend on each other
- [ ] Tests cover: start on Timers, switching tabs, back returns to Timers

---

## 0.2 Design system

**Size:** L · **Depends on:** —
**Design:** [Figma › Components v3](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=23-105) and the "Stovetime palette" and "Stovetime theme" variable collections (see roadmap §11)

### Description
Create `:presentation:design` with the colours, type, spacing and shapes from the Figma file, plus the first shared components: button, ticket (rail, order and step), category, chip, list row, nav bar and empty state.

### Acceptance Criteria
- [ ] Every token in the Figma file is available in code, and colour values are defined in one place only
- [ ] Colours follow the two layers in Figma: palette values in one file, Material 3 roles in `ColorScheme` for light and dark, and the extended roles (success, highlight, ticket, hero) in a `StovetimeColors` class provided through a CompositionLocal
- [ ] Light and dark themes both work
- [ ] Each component has a preview of every variant and state
- [ ] Previews render at font scale 1.5 without clipping
- [ ] Tests cover: theme missing fails with a clear message, disabled button ignores taps

---

## 0.3 Local database and preferences

**Size:** M · **Depends on:** —

### Description
Add Room for timers, recipes, the shopping list and the cookbook, and DataStore for small settings (units, sound, keep screen on).

### Acceptance Criteria
- [ ] `:datasources:local` holds the Room database and DataStore, with schemas exported for migrations
- [ ] Repositories in `:core:data` expose flows; nothing above them touches Room directly
- [ ] Settings survive a restart
- [ ] Tests cover: insert and read back, a settings change is emitted, migration from version 1 (once version 2 exists)

---

## 0.4 Timer engine

**Size:** L · **Depends on:** 0.3

### Description
Create `:sdk:timer`, the part everything else builds on. It runs any number of timers, keeps them alive in a foreground service with one ongoing notification, rings when a timer ends even if the app was swiped away, and restores running timers after the process is killed.

### Acceptance Criteria
- [ ] Start, pause, resume, add time and cancel work for any number of timers at once
- [ ] Times are based on the wall clock, so a timer stays accurate after the app is killed and restarted
- [ ] One ongoing notification lists every running timer and updates every second while visible
- [ ] When a timer ends, it rings and vibrates even with the app closed or the screen locked
- [ ] Running timers are restored after process death and after a reboot
- [ ] The notification permission is asked for the first time a timer starts, with an explanation first
- [ ] Tests cover: two timers end in the right order, pause/resume keeps the remaining time, restore after process death, a timer that ended while the app was dead rings immediately on restore

---

## 0.5 CI and README

**Size:** S · **Depends on:** 0.1

### Description
Run the quality checks on every pull request, and write a README that lets anyone clone, build and run the app, and copy the agent setup in `.ai/`.

### Acceptance Criteria
- [ ] A GitHub Action runs build, unit tests and ktlint on pull requests
- [ ] The README covers setup, modules, the roadmap and how the `.ai/` skills and agents are used
- [ ] A new developer can clone, build and run the app by following the README alone
