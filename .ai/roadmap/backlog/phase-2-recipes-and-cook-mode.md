# Phase 2 — Recipes & cook mode

**Order:** 2.1 → 2.2 → 2.3 → 2.4 → 2.5 → 2.6

## 2.1 Recipe model and storage

**Size:** M · **Depends on:** 0.3

### Description
Store recipes on the device: title, photo, servings, prep and cook time, ingredients (amount, unit, name, optional note) and ordered steps.

### Acceptance Criteria
- [ ] Recipes, ingredients and steps are saved in Room and exposed as flows by a repository
- [ ] Ingredient amounts are stored as numbers with a unit, so they can be scaled and converted
- [ ] Deleting a recipe removes its ingredients and steps
- [ ] Tests cover: save and read back, delete cascades, ingredient parsing ("1 ½ cups flour")

---

## 2.2 Add and edit a recipe

**Size:** M · **Depends on:** 2.1, 0.2

### Description
Type in a family recipe: title, photo, servings, ingredients and steps. Paste a block of ingredients and the app splits it into lines.

### Acceptance Criteria
- [ ] All fields from 2.1 can be added and edited; steps can be reordered
- [ ] Pasting several ingredient lines creates one ingredient per line, with amount and unit recognised where possible
- [ ] Leaving with unsaved changes asks before discarding them
- [ ] Tests cover: paste splitting, reorder saves the new order, discard prompt

---

## 2.3 Recipe screen, scaling and units

**Size:** M · **Depends on:** 2.1

### Description
Read a recipe, change the servings and see every amount update, and switch between metric and imperial.

### Acceptance Criteria
- [ ] Changing servings scales every amount, with sensible rounding (no "0.333 eggs")
- [ ] Metric/imperial switches amounts and oven temperatures
- [ ] Ingredients can be ticked off while preparing
- [ ] Tests cover: scaling and rounding, unit conversion for weight, volume and temperature

---

## 2.4 Cook mode

**Size:** M · **Depends on:** 2.3

### Description
A full-screen mode for cooking: one step at a time in big text, the screen stays on, and the next step is one large tap or swipe away.

### Acceptance Criteria
- [ ] One step per screen in large text, with step number and total
- [ ] The screen stays awake while cook mode is open
- [ ] Next and back are large targets on both sides of the screen
- [ ] Running timers stay visible along the top
- [ ] Tests cover: navigation through steps, keep-awake is released when leaving

---

## 2.5 Tap-to-start times in steps

**Size:** M · **Depends on:** 2.4, 1.2

### Description
Find times inside step text ("simmer for 20 minutes", "bake 25–30 min") and turn them into buttons that start a named timer.

### Acceptance Criteria
- [ ] Times written as minutes, hours, seconds and ranges are recognised
- [ ] A range starts at the lower time and offers +5 min when it ends
- [ ] The timer is named after the recipe and step ("Pilau — simmer")
- [ ] Tests cover: time detection for each format, range handling, naming

---

## 2.6 Starter recipes

**Size:** S · **Depends on:** 2.1, 1.3

### Description
Ship a small set of recipes so the app is useful on first launch, including a few Kenyan favourites, each linked to the guides where they apply.

### Acceptance Criteria
- [ ] At least 12 recipes, including pilau, chapati and sukuma wiki
- [ ] Every recipe has at least one tap-to-start time
- [ ] Starter recipes can be saved to the cookbook and edited like your own
- [ ] Tests cover: every starter recipe parses and has a timed step
