# Phase 5 — Later

**Order:** 5.1 → 5.2 → 5.3 → 5.4 → 5.5

Phases 0–4 shipped on 2026-11-02, so the Phase 5 ideas from the roadmap become steps. Each one builds on what's there and keeps the principles: cooking first, encourage don't nag, and the Path works offline.

## 5.1 More courses

**Size:** L · **Depends on:** 1.1, 1.2
**Design:** follow [Figma › Home — Path](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=70-98); the course switcher reuses the bottom sheet from [Timers — Quick timer](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=81-194)

### Description
Three more courses next to "Home basics": "Rice & grains", "One-pan dinners" and "Kenyan favourites" (pilau, chapati, sukuma wiki). Tapping the course chip on the Path opens a sheet listing every course with how many lessons are cooked; choosing one shows its path. Each course unlocks in order on its own; XP and the week streak count every cook.

### Acceptance Criteria
- [ ] Four courses ship inside the app in the format of roadmap §6, each with one unit of at least three lessons, every lesson meeting the 1.1 content rules
- [ ] "Kenyan favourites" has pilau, chapati and sukuma wiki
- [ ] Tapping the course chip opens a sheet with every course, its title and "N of M cooked"; choosing one shows that course's path and the chip shows its title
- [ ] Each course unlocks its own lessons in order; cooking in one course never unlocks lessons in another
- [ ] The chosen course is remembered across app restarts
- [ ] A lesson with no bundled photo shows a plain placeholder, never a broken image
- [ ] Tests cover: every course file passes the content rules, lesson ids are unique across courses, per-course unlocking, switching course, the remembered course

### Edge cases
- A stored course id that no longer exists falls back to "Home basics".
- The same dish name in two courses is two lessons with different ids.

### Out of scope
- Downloading courses from a server
- Photography for the new lessons (placeholders until the photos are shot)

---

## 5.2 Badges

**Size:** M · **Depends on:** 1.5, 4.1

### Description
Small rewards for real cooking: a badge for finishing every lesson in a unit, and badges for week streaks of 2, 4 and 8 weeks (best streak, so they're never taken away). They show on Me, and the finish screen celebrates a badge the moment it's earned.

### Acceptance Criteria
- [ ] Earning rules: "Unit done" per unit when all its lessons are cooked; "2-week streak", "4-week streak", "8-week streak" from the best week streak
- [ ] Me shows every badge, earned ones in colour and the rest greyed with how to earn them
- [ ] The finish screen shows "New badge!" with the badge when this cook earned one
- [ ] Badges are worked out from progress, so Start over removes them and they come back as you cook again
- [ ] TalkBack reads each badge with its name and whether it's earned
- [ ] Tests cover: a unit badge on the last lesson, streak badges at 2, 4 and 8 weeks, a badge kept after the streak resets, the finish screen's new badge

### Out of scope
- Sharing badges

---

## 5.3 Weekly goal reminder

**Size:** M · **Depends on:** 1.5, 4.2

### Description
One gentle nudge when the week's goal is in reach: on Saturday evening, if you're one or two cooks short, a notification says how many cooks are left to keep the streak. Never on other days, never when the goal is already met, and it can be switched off in Me.

### Acceptance Criteria
- [ ] A background check runs once a day and posts at most one reminder a week, on Saturday after 17:00 in the phone's time zone
- [ ] The reminder only shows when the week has 1 or 2 cooks left to reach the goal
- [ ] Its text says how many cooks are left ("One more cook keeps your 3-week streak going"), and tapping it opens the Path
- [ ] Me has a "Weekly reminder" switch, on by default; switching it off stops reminders
- [ ] No reminder without the notification permission; nothing asks for it just for reminders
- [ ] Tests cover: the reminder rule (day, time, cooks left, goal met, already sent this week), the switch, the text with and without a streak

### Out of scope
- Daily reminders or choosing the reminder time

---

## 5.4 Dark mode

**Size:** M · **Depends on:** 0.1

### Description
A dark version of the tactile flat look that follows the phone's dark theme: warm dark surfaces, the same tomato, basil and yolk accents, and ledges that still read. Status and navigation bar icons switch to light on dark.

### Acceptance Criteria
- [ ] Every colour token has a dark value, defined once in `:presentation:design`; the app follows the phone's light or dark setting
- [ ] Text keeps at least 4.5:1 contrast on its background in both themes
- [ ] Status-bar and navigation-bar icons are readable in both themes, and the alarm stays red with white text
- [ ] Every component preview also renders in dark
- [ ] Tests cover: every light token has a dark counterpart, contrast of text tokens on their backgrounds in both themes

### Edge cases
- Switching the phone's theme while the app is open updates it without losing state.

### Out of scope
- An in-app theme switch

---

## 5.5 Running timers widget

**Size:** M · **Depends on:** 2.1

### Description
A home-screen widget that shows the running timers, soonest first, with each one's name and time left counting down, and a button for a new quick timer. Tapping a timer opens it like its notification does.

### Acceptance Criteria
- [ ] The widget lists up to three running or paused timers, soonest-ending first, with name and a live countdown (or "Paused" / "Time's up")
- [ ] With no timers it shows "No timers running" and a "Quick timer" button that opens the Timers tab
- [ ] Tapping a timer opens its cook step or the Timers tab, the same as tapping its notification
- [ ] The widget updates as soon as a timer starts, changes or ends, without the app open
- [ ] Tests cover: the widget's list (order, the three-timer cap, empty), the tap targets

### Out of scope
- Starting or pausing timers from the widget
