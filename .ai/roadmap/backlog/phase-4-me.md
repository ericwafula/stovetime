# Phase 4 — Me

**Order:** 4.1 → 4.2 → 4.3

## 4.1 Me screen

**Size:** M · **Depends on:** 1.5, 0.3
**Design:** needs a frame

### Description
Your progress at a glance: total XP, the current and best week streak, lessons and recipes cooked, this week's cooks against the goal, and a history of everything you've cooked, newest first, with its date, rating and XP.

### Acceptance Criteria
- [ ] Total XP, current streak, best streak and number of cooks match the progress rules
- [ ] This week's row shows the same dots and "N of goal cooks" as the finish screen
- [ ] The history lists every cook, newest first, with the dish name, photo, date, rating and XP
- [ ] Tapping a history item opens that lesson's intro or that recipe's page
- [ ] Tests cover: totals on a fresh install, totals after several cooks, best streak stays after a reset, history order

### Edge cases
- Nothing cooked yet: an empty history that points back to the Path.
- A recipe removed from TheMealDB but in the history still shows its stored name and photo.

### Out of scope
- Charts and stats over time

---

## 4.2 Settings

**Size:** S · **Depends on:** 4.1

### Description
Two settings in Me: the weekly cook goal (1 to 7) and "keep screen on while cooking".

### Acceptance Criteria
- [ ] The weekly goal can be set from 1 to 7 and shows straight away on the Path, finish screen and Me
- [ ] "Keep screen on while cooking" switches cook mode's screen-on behaviour
- [ ] Both settings survive restarting the app
- [ ] Tests cover: changing the goal updates the week row, the goal can't go below 1 or above 7, the screen-on setting is read by cook mode

### Edge cases
- Lowering the goal so this week is already met: this week counts as met straight away.

### Out of scope
- Notifications and reminders (Phase 5)

---

## 4.3 Start over

**Size:** S · **Depends on:** 4.1

### Description
Reset all progress, for someone handing over their phone or wanting a fresh start.

### Acceptance Criteria
- [ ] "Start over" asks for confirmation, saying it deletes all XP, streaks and history
- [ ] Confirming clears progress; the Path goes back to the first lesson and everything shows 0
- [ ] Settings and the synced recipes are kept
- [ ] Cancelling changes nothing
- [ ] Tests cover: confirm clears progress, cancel keeps it, settings and recipes survive

### Out of scope
- Undo after confirming
