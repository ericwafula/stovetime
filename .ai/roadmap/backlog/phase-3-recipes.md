# Phase 3 — Recipes (TheMealDB)

**Order:** 3.1 → 3.2 → 3.3 → 3.4 → 3.5

The phone's database is the source of truth: the Recipes tab only reads recipes from the phone, and a background sync keeps them fresh. Full recipes are fetched the first time one is opened and kept after that.

## 3.1 Recipe sync ✅

**Size:** L · **Depends on:** 0.4, 0.5

### Description
Keep a copy of TheMealDB's catalogue on the phone: every category and the meals in it (name, photo, category). It syncs in the background on first launch and about once a day, so browsing and search work without a connection and open instantly.

### Acceptance Criteria
- [x] The first launch with a connection fills the catalogue in the background, without blocking the app
- [x] After that, the catalogue refreshes about once a day, only when the phone has a connection
- [x] A failed sync retries later on its own and never shows an error unless the user is on the Recipes tab with nothing stored
- [x] A sync updates changed meals and adds new ones, without duplicates
- [x] Meals that disappear from TheMealDB are removed, unless the user has cooked them
- [x] The time of the last successful sync is stored
- [x] Tests cover: first sync fills the catalogue, a second sync updates without duplicating, a removed meal is deleted, a cooked meal is kept, a failed sync keeps the old data

### Edge cases
- The app is closed or the phone restarts mid-sync: the next run starts again cleanly.
- Two syncs are asked for at once (scheduled and pull-to-refresh): only one runs.
- `{"meals": null}` for a category: that category is stored as empty, not as a failure.

### Out of scope
- Syncing full recipe details for every meal (fetched on open, 3.3)
- Uploading anything; there is no backend

---

## 3.2 Browse and search ✅

**Size:** M · **Depends on:** 3.1, 0.3
**Design:** [Figma › Recipes — Browse](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=80-48)

### Description
The Recipes tab: category chips across the top, a grid of meal cards with photos, and a search box. Everything comes from the synced catalogue, so it works offline. Pull down to sync now.

### Acceptance Criteria
- [x] The tab opens on "All" with every stored meal, newest sync first
- [x] Tapping a category shows only its meals
- [x] Typing in search filters by meal name as you type, ignoring case and accents
- [x] Pull to refresh runs a sync and shows when it last synced
- [x] Tapping a card opens its recipe page (3.3)
- [x] Tests cover: category filter, search ignoring case and accents, empty search result, pull to refresh starts a sync

### Edge cases
- Nothing synced yet and no connection: an empty state that explains why, with a retry.
- Nothing synced yet but a sync is running: a loading state, not the empty one.
- A search with no matches: "No recipes match 'xyz'".

### Out of scope
- Searching TheMealDB live for meals not in the catalogue
- Favourites (Phase 5)

---

## 3.3 Recipe page ✅

**Size:** M · **Depends on:** 3.2
**Design:** follow [Figma › Recipe — Intro](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-120) until it has its own frame

### Description
A recipe from TheMealDB in the same layout as a lesson intro: photo, name, category and area, "+10 XP", the ingredients with measures, and a Start cooking button. The full recipe is fetched the first time it's opened and kept on the phone after that.

### Acceptance Criteria
- [x] A recipe opened before shows straight away, even offline
- [x] A recipe never opened before shows a loading state, then the recipe
- [x] Each ingredient shows with its measure, and can be ticked off like the lesson intro
- [ ] Start cooking opens cook mode for this recipe (3.4)
- [x] Tests cover: opened before and offline, first open online, first open offline

### Edge cases
- First open with no connection: an error that says the recipe needs a connection the first time, with a retry.
- A recipe with no photo shows a plain placeholder.

### Out of scope
- Showing the YouTube video link

---

## 3.4 Cook any recipe

**Size:** M · **Depends on:** 3.3, 1.4, 1.5

### Description
Cook a TheMealDB recipe in the same cook mode as a lesson. Its instructions are split into steps, and any time in a step ("simmer for 20 minutes") becomes a timer. Finishing it earns XP and counts towards the weekly goal.

### Acceptance Criteria
- [ ] Instructions are split into steps on line breaks, or on sentences when there are none
- [ ] A step that mentions a time ("20 minutes", "1 hour", "1½ hours", "30-40 mins") gets a timer set to it (the lower number for a range)
- [ ] Finishing earns +10 XP and counts as a cook for the week and the streak
- [ ] The finish screen and Me history show the recipe's name and photo
- [ ] Tests cover: splitting on line breaks, splitting on sentences, each time format, a range, a step with no time

### Edge cases
- Instructions that are one long paragraph with no full stops: one step with the whole text.
- Numbered lines ("1.", "STEP 1") lose the number.
- "Bake 10 minutes then rest 5 minutes": the first time becomes the timer.

### Out of scope
- Tips and "what you'll learn" (lessons only)

---

## 3.5 Offline and sync states

**Size:** S · **Depends on:** 3.2, 3.3

### Description
Make it clear when the Recipes tab is working from the phone's copy, and what to do when something can't load.

### Acceptance Criteria
- [ ] Offline, the tab shows a small "Offline · last synced 2 days ago" note and everything stored still works
- [ ] A sync that keeps failing for over a week shows a gentle note with a retry
- [ ] Coming back online triggers a sync if the last one is over a day old
- [ ] Tests cover: the offline note, the stale-sync note, a sync when the connection returns

### Out of scope
- Letting the user choose how often it syncs
