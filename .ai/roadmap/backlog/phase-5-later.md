# Phase 5 — Later

**Order:** 5.1 → 5.2 → 5.3 → 5.4 → 5.5 → 5.6

Phases 0–4 shipped on 2026-11-02, so the Phase 5 ideas from the roadmap become steps. Each one builds on what's there and keeps the principles: cooking first, encourage don't nag, and the Path works offline.

## 5.1 More courses ✅

**Size:** L · **Depends on:** 1.1, 1.2
**Design:** follow [Figma › Home — Path](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=70-98); the course switcher reuses the bottom sheet from [Timers — Quick timer](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-194)

### Description
Three more courses next to "Home basics": "Rice & grains", "One-pan dinners" and "Kenyan favourites" (pilau, chapati, sukuma wiki). Tapping the course chip on the Path opens a sheet listing every course with how many lessons are cooked; choosing one shows its path. Each course unlocks in order on its own; XP and the week streak count every cook.

### Acceptance Criteria
- [x] Four courses ship inside the app in the format of roadmap §6, each with one unit of at least three lessons, every lesson meeting the 1.1 content rules
- [x] "Kenyan favourites" has pilau, chapati and sukuma wiki
- [x] Tapping the course chip opens a sheet with every course, its title and "N of M cooked"; choosing one shows that course's path and the chip shows its title
- [x] Each course unlocks its own lessons in order; cooking in one course never unlocks lessons in another
- [x] The chosen course is remembered across app restarts
- [x] A lesson with no bundled photo shows a plain placeholder, never a broken image
- [x] Tests cover: every course file passes the content rules, lesson ids are unique across courses, per-course unlocking, switching course, the remembered course

### Edge cases
- A stored course id that no longer exists falls back to "Home basics".
- The same dish name in two courses is two lessons with different ids.

### Out of scope
- Downloading courses from a server
- Photography for the new lessons (placeholders until the photos are shot)

---

## 5.2 Badges ✅

**Size:** M · **Depends on:** 1.5, 4.1

### Description
Small rewards for real cooking: a badge for finishing every lesson in a unit, and badges for week streaks of 2, 4 and 8 weeks (best streak, so they're never taken away). They show on Me, and the finish screen celebrates a badge the moment it's earned.

### Acceptance Criteria
- [x] Earning rules: "Unit done" per unit when all its lessons are cooked; "2-week streak", "4-week streak", "8-week streak" from the best week streak
- [x] Me shows every badge, earned ones in colour and the rest greyed with how to earn them
- [x] The finish screen shows "New badge!" with the badge when this cook earned one
- [x] Badges are worked out from progress, so Start over removes them and they come back as you cook again
- [x] TalkBack reads each badge with its name and whether it's earned
- [x] Tests cover: a unit badge on the last lesson, streak badges at 2, 4 and 8 weeks, a badge kept after the streak resets, the finish screen's new badge

### Out of scope
- Sharing badges

---

## 5.3 Weekly goal reminder ✅

**Size:** M · **Depends on:** 1.5, 4.2

### Description
One gentle nudge when the week's goal is in reach: on Saturday evening, if you're one or two cooks short, a notification says how many cooks are left to keep the streak. Never on other days, never when the goal is already met, and it can be switched off in Me.

### Acceptance Criteria
- [x] A background check runs once a day and posts at most one reminder a week, on Saturday after 17:00 in the phone's time zone
- [x] The reminder only shows when the week has 1 or 2 cooks left to reach the goal
- [x] Its text says how many cooks are left ("One more cook keeps your 3-week streak going"), and tapping it opens the Path
- [x] Me has a "Weekly reminder" switch, on by default; switching it off stops reminders
- [x] No reminder without the notification permission; nothing asks for it just for reminders
- [x] Tests cover: the reminder rule (day, time, cooks left, goal met, already sent this week), the switch, the text with and without a streak

### Out of scope
- Daily reminders or choosing the reminder time

---

## 5.4 Dark mode ✅

**Size:** M · **Depends on:** 0.1

### Description
A dark version of the tactile flat look that follows the phone's dark theme: warm dark surfaces, the same tomato, basil and yolk accents, and ledges that still read. Status and navigation bar icons switch to light on dark.

### Acceptance Criteria
- [x] Every colour token has a dark value, defined once in `:presentation:design`; the app follows the phone's light or dark setting
- [x] Text keeps at least 4.5:1 contrast on its background in both themes
- [x] Status-bar and navigation-bar icons are readable in both themes, and the alarm stays red with white text
- [x] Every component preview also renders in dark
- [x] Tests cover: every light token has a dark counterpart, contrast of text tokens on their backgrounds in both themes

### Edge cases
- Switching the phone's theme while the app is open updates it without losing state.

### Out of scope
- An in-app theme switch

---

## 5.5 Running timers widget ✅

**Size:** M · **Depends on:** 2.1

### Description
A home-screen widget that shows the running timers, soonest first, with each one's name and time left counting down, and a button for a new quick timer. Tapping a timer opens it like its notification does.

### Acceptance Criteria
- [x] The widget lists up to three running or paused timers, soonest-ending first, with name and a live countdown (or "Paused" / "Time's up")
- [x] With no timers it shows "No timers running" and a "Quick timer" button that opens the Timers tab
- [x] Tapping a timer opens its cook step or the Timers tab, the same as tapping its notification
- [x] The widget updates as soon as a timer starts, changes or ends, without the app open
- [x] Tests cover: the widget's list (order, the three-timer cap, empty), the tap targets

### Out of scope
- Starting or pausing timers from the widget

---

## 5.6 Scale servings ⬜

**Size:** M · **Depends on:** 3.3
**Design:** no frame yet. A − / + stepper under the ingredients heading on [Figma › Recipe — Details](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=106-103), built from the design system's buttons and text styles

### Description
On a recipe page, a "Serves 4" stepper sets how many people you're cooking for, and every measure with a number in it changes to match. TheMealDB doesn't say how many a recipe serves, so every recipe starts at 4.

### Acceptance Criteria
- [ ] The recipe page shows "Serves 4" with − and + buttons, from 1 up to 12
- [ ] Measures with a number scale with the servings: "200g" for 4 is "100g" for 2, and "1 1/2 cups" for 4 is "3 cups" for 8
- [ ] Scaled amounts show as whole numbers or kitchen fractions (½, ⅓, ¼, ¾), rounded to the nearest quarter and never below ¼, never as long decimals like 0.333
- [ ] Measures without a number ("pinch", "to taste", "garnish") stay as they are
- [ ] Ticked ingredients stay ticked when the servings change
- [ ] Tests cover: reading "1/2", "1 1/2", "2-3", "200g" and "1.5 kg"; measures without numbers; the 1 and 12 limits; rounding to kitchen fractions; ticks surviving a change

### Edge cases
- Ranges scale both ends: "2-3 cloves" for 4 is "4-6 cloves" for 8.
- The unit stays the same: "500g" for 4 is "1500g" for 12, not "1.5 kg".
- "1 egg" for 4 is "¼ egg" for 1. We don't guess which ingredients can't be split.
- A measure that can't be read stays exactly as TheMealDB wrote it.
- Opening the recipe again starts back at 4.

### Out of scope
- Amounts written inside the step text in cook mode
- Converting units (cups to grams, g to kg)
- Scaling lesson ingredients (lessons are written for the servings they teach)
- Remembering the servings for each recipe
- Changing timer lengths
