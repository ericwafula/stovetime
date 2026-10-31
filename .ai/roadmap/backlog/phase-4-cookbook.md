# Phase 4 — My cookbook

**Order:** 4.1 → 4.2 → 4.5 → 4.3 → 4.4

## 4.1 Favourites and collections

**Size:** M · **Depends on:** 2.1

### Description
Organise recipes the way you cook: favourites, plus your own collections like "Weeknight" or "Sunday lunch".

### Acceptance Criteria
- [ ] Any recipe can be favourited and added to one or more collections
- [ ] Collections can be created, renamed and deleted (recipes stay)
- [ ] The Cookbook tab shows favourites and every collection with a count
- [ ] Tests cover: a recipe in two collections, deleting a collection keeps its recipes

---

## 4.2 "I cooked this"

**Size:** M · **Depends on:** 2.4

### Description
At the end of cook mode, log that you cooked it, with an optional note ("less salt next time") and a rating. Your notes show on the recipe next time.

### Acceptance Criteria
- [ ] Finishing cook mode offers "I cooked this" with an optional note and a 1–5 rating
- [ ] The recipe shows how many times you've cooked it, when, and your latest note
- [ ] Notes can be edited or deleted later
- [ ] Tests cover: logging adds to the history, latest note is shown

---

## 4.3 Import from a web link

**Size:** L · **Depends on:** 2.1

### Description
Share a recipe page from the browser to Stovetime and save it, using the page's schema.org Recipe data.

### Acceptance Criteria
- [ ] Stovetime appears in the Android share sheet for links
- [ ] Pages with schema.org Recipe data import title, photo, servings, ingredients and steps for review before saving
- [ ] Pages without it show a clear "we couldn't find a recipe on this page" message
- [ ] The source link is kept on the recipe
- [ ] Tests cover (fixture pages): a full recipe, missing fields, no recipe data

---

## 4.4 Share a recipe

**Size:** S · **Depends on:** 2.3

### Description
Send a recipe to a friend as text, or as an image card for WhatsApp status.

### Acceptance Criteria
- [ ] Share as text includes ingredients and steps at the current servings
- [ ] Share as card creates an image with photo, title and ingredients
- [ ] Tests cover: text format, card renders for a recipe with no photo

---

## 4.5 Cookbook home

**Size:** S · **Depends on:** 4.1, 4.2

### Description
Make the Cookbook tab feel like your own book: recently cooked, most cooked, and recipes you saved but never tried.

### Acceptance Criteria
- [ ] Sections for recently cooked, most cooked and "never tried", each hidden when empty
- [ ] An empty cookbook points to the starter recipes and the import option
- [ ] Tests cover: section contents and ordering, empty state
