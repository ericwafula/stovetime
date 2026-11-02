# Phase 0 — Foundations

**Order:** 0.1 ✅ → 0.2 → 0.3 → 0.4 → 0.5

## 0.1 Design system ✅

**Size:** L · **Depends on:** —
**Design:** [Figma › Components](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN) and the "Home palette", "Home theme" and "Home size" variable collections

### Description
The look of the app in one module: colours, type, spacing, shapes and the shared components (button, path node, stat chip, timer chip, pill, progress bar, nav bar, card), so every screen looks the same without repeating values.

### Acceptance Criteria
- [x] Every colour, type style, spacing and radius from Figma is available in code, defined once
- [x] Components: button (primary, success, secondary), path node (done, current, locked), stat chip, timer chip, pill, progress bar, nav bar, card
- [x] Pressable components drop onto their ledge when pressed
- [x] Every component has a preview
- [x] Using a component outside the theme fails with a clear message

### Out of scope
- Dark mode (Phase 5)

---

## 0.2 Module structure and shared basics ✅

**Size:** M · **Depends on:** 0.1

### Description
Lay out the modules from roadmap §5 that Phase 1 needs first, with the pieces every feature shares: one result type for data calls, coroutine dispatchers, and the ViewModel base every screen uses.

### Acceptance Criteria
- [x] The app still builds, runs and shows the same first screen after the split
- [x] Data calls return a success or a typed error; an error never crashes the app
- [x] Screens get their state from a ViewModel that takes user actions and sends one-off events (navigate, show a sheet)
- [x] Dependency injection is set up once in the app, and each module adds its own pieces
- [x] Tests cover: a failed data call becomes a typed error, a cancelled call is not turned into an error, a ViewModel action changes state

### Edge cases
- A module nothing uses yet isn't created; it's added by the first step that needs it.

### Out of scope
- `:sdk:timer` (2.1), `:datasources:network` (0.5)

---

## 0.3 Navigation and the bottom bar ✅

**Size:** M · **Depends on:** 0.2
**Design:** [Figma › Home — Path](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=70-98) (the floating nav bar)

### Description
The four areas of the app, Path, Recipes, Timers and Me, reachable from the floating bottom bar. Each tab is a placeholder screen until its phase lands. The app draws edge to edge with dark status-bar icons on the cream background.

### Acceptance Criteria
- [x] The app opens on Path
- [x] Tapping a tab opens its screen and highlights the tab
- [x] Back from any other tab returns to Path; back on Path closes the app
- [x] Rotation and coming back from the background keep the same tab
- [x] Status-bar icons are dark and readable on the cream background, in light and dark phone settings
- [x] Screens opened from a tab (lesson intro, cook mode) hide the bar
- [x] Tests cover: start on Path, switching tabs, back returns to Path

### Edge cases
- The phone is in dark mode: the app stays in its light theme and the status bar stays readable.

### Out of scope
- The real content of each tab (Phases 1–4)

---

## 0.4 Local storage ✅

**Size:** M · **Depends on:** 0.2

### Description
Store progress on the phone: which lessons and recipes were cooked, when, how long it took, the rating and the XP earned. Store settings separately: the weekly goal and "keep screen on".

### Acceptance Criteria
- [x] Progress and settings survive closing the app and restarting the phone
- [x] Screens see progress change live, without reopening them
- [x] A fresh install has no progress, a weekly goal of 4 and "keep screen on" switched on
- [x] The database schema is exported, so later versions can migrate without losing progress
- [x] Tests cover: save and read back a cook, a settings change is seen straight away, a fresh install's defaults

### Edge cases
- Two cooks of the same lesson are two separate records, not one.

### Out of scope
- Cloud backup or sync (no account in v1)

---

## 0.5 TheMealDB client ✅

**Size:** M · **Depends on:** 0.2

### Description
Talk to TheMealDB: list categories, list the meals in a category, search by name, and look up one meal. This is what the Recipes tab (Phase 3) is built on.

### Acceptance Criteria
- [x] Categories, meals in a category, search by name and look up by id all return data
- [x] "Nothing found" (`{"meals": null}`) is an empty result, not an error
- [x] No connection, a timeout and a server error each come back as their own typed error
- [x] Ingredients and measures (up to 20 of each) come back as a clean list with empty slots dropped
- [x] The base URL and key are configured in one place
- [x] Tests cover: a normal response, `{"meals": null}`, ingredient pairing with gaps, no connection, a server error

### Edge cases
- An ingredient with no measure keeps the ingredient with an empty measure.
- Search text with spaces or accents is encoded properly.

### Out of scope
- Caching (3.4) and any screen (3.1)
