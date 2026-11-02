# Phase 1 — The Path

**Order:** 1.1 → 1.5 → 1.2 → 1.3 → 1.4 → 1.6 → 1.7

1.5 comes early because the Path, the finish screen and Me all read the same XP, unlock and streak rules (roadmap §8).

## 1.1 Course content ✅

**Size:** M · **Depends on:** 0.2

### Description
Write the first course, "Home basics", with Unit 1 "Crack it: five ways with eggs": Boiled eggs, Scrambled eggs, Jammy eggs on toast, French omelette and Shakshuka. Each lesson has a photo, time, difficulty, XP, what you'll learn, what you'll need and its steps, with a timer and a doneness check on every timed step. It ships inside the app in the format of roadmap §6. The five lesson photos are ready in `.ai/roadmap/content/photos/`: move them into the resources of the module that owns the course content, keeping their names.

### Acceptance Criteria
- [x] The app can read the course, its unit and all five lessons, in order, with no connection
- [x] Every lesson has a title, photo, minutes, difficulty, XP, at least one "what you'll learn", ingredients and at least two steps
- [x] Every step that says to wait has a timer and says what done looks like
- [x] Each lesson's photo is its matching file from `.ai/roadmap/content/photos/`, and that folder is empty afterwards
- [x] Jammy eggs on toast matches the Figma frames (15 min, Easy, +30 XP, a 6:30 timer on step 2)
- [x] A broken course file fails a test, not the app at runtime
- [x] Tests cover: the course loads, lessons are in order, every timed step has a doneness check, lesson ids are unique

### Edge cases
- A step with a tip but no timer is valid; a timer without a doneness check is not.

### Out of scope
- More units or courses (Phase 5)
- Downloading courses from a server

---

## 1.2 Path screen

**Size:** L · **Depends on:** 1.1, 1.5, 0.3, 0.4
**Design:** [Figma › Home — Path](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=70-98)

### Description
The home screen. At the top, the course chip ("Home basics"), the week streak and total XP. Below it, the unit banner with how many lessons are cooked, then the lessons as a winding path of nodes: done, current or locked. The current lesson has an "Up next" card with its time and a Start button showing the XP it's worth.

### Acceptance Criteria
- [ ] The streak and XP chips show the real week streak and total XP
- [ ] The unit banner shows the unit number, title and "N of 5 cooked", with a matching progress bar
- [ ] Cooked lessons show as done, the next one as current with its photo, and the rest as locked
- [ ] The "Up next" card shows the current lesson's title, minutes and "Start · +N XP"
- [ ] Tapping Start or the current node opens the lesson intro (1.3)
- [ ] Tapping a done node opens its intro to cook it again
- [ ] Tapping a locked node says "Finish the previous lesson first" and opens nothing
- [ ] Progress updates as soon as you come back from a finished cook, without a reload
- [ ] Tests cover: a fresh install (first lesson current, the rest locked), mid-unit progress, a finished unit, tapping a locked node

### Edge cases
- A fresh install: streak 0 and 0 XP show as "0", never blank.
- Every lesson in the unit is cooked: no "Up next" card; the banner says the unit is done and every node is done.
- A very long lesson title wraps inside the card and never pushes the Start button off screen.

### Out of scope
- Switching courses from the course chip (only one course in v1)
- Animations when a node unlocks (Phase 5)

---

## 1.3 Lesson intro

**Size:** M · **Depends on:** 1.2
**Design:** [Figma › Recipe — Intro](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-120)

### Description
Everything to know before you start: a big photo, "Unit 1 · Recipe 3 of 5", the title, time, difficulty and XP pills, "What you'll learn" and "You'll need" as a checklist you can tick while you gather things. Start cooking opens cook mode.

### Acceptance Criteria
- [ ] Shows the lesson's photo, position in the unit, title, minutes, difficulty and XP
- [ ] "What you'll learn" lists every point from the lesson
- [ ] Each "You'll need" item can be ticked and unticked; ticks are kept while the screen is open, including rotation
- [ ] Start cooking opens cook mode on step 1 and records the start time
- [ ] The close button returns to the Path
- [ ] Tests cover: the content shown, ticking an ingredient, Start opens step 1, close goes back

### Edge cases
- Start cooking works with nothing ticked; the list is a helper, not a gate.
- A lesson cooked before shows "Cook again · +10 XP" instead of its full XP.

### Out of scope
- Scaling servings or converting units

---

## 1.4 Cook mode

**Size:** L · **Depends on:** 1.3
**Design:** [Figma › Cook — Step](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-158)

### Description
One step at a time. A progress bar with "2/5", the step number, the step in big text and its detail. A timed step has a timer card showing the time and what done looks like, with Start timer. A tip, if the step has one. Next step moves on; the last step's button is Finish. The screen stays on while cooking.

### Acceptance Criteria
- [ ] The progress bar and "N/total" match the step you're on
- [ ] Next step moves forward; back (system back or a back gesture) goes to the previous step, and from step 1 asks to leave
- [ ] A timed step shows its time and doneness check; Start timer counts down on the card and turns into pause and reset
- [ ] When a step timer ends, the phone rings and vibrates and the card says the time is up
- [ ] The last step's button says Finish and opens the finish screen (1.6)
- [ ] The close button asks "Leave this cook? Your progress on this recipe won't be saved" before leaving
- [ ] The screen stays on while cook mode is open (if the setting is on), and the step survives rotation
- [ ] Tests cover: next and back, the last step finishes, leaving asks first, a timer counts down and ends

### Edge cases
- Moving to the next step while a timer runs keeps the timer running, shown as a small timer chip.
- The app goes to the background mid-cook: coming back shows the same step and the timer's real remaining time.

### Out of scope
- Timers that ring with the app closed and the Timers tab (Phase 2; here the timer only has to work while the app is open)
- Voice control

---

## 1.5 Progress rules ✅

**Size:** M · **Depends on:** 0.4

### Description
The rules from roadmap §8 in one place, so the Path, finish screen and Me always agree: how much XP a cook earns, which lessons are unlocked, how many cooks there are this week, and the week streak.

### Acceptance Criteria
- [x] A first cook of a lesson earns its XP; cooking it again earns +10
- [x] Lessons unlock in order: the next opens when the one before it is cooked
- [x] "This week" counts every cook from Monday 00:00 to Sunday 23:59 in the phone's time zone
- [x] The week streak counts weeks in a row that met the weekly goal, including this week once it's met
- [x] Total XP is the sum of every cook's XP
- [x] Tests cover, with fixed dates: first cook and repeat XP, unlock order, a cook at 23:59 on Sunday, a cook at 00:00 on Monday, a streak of 3, a missed week resets to 0, this week not yet met keeps last week's streak

### Edge cases
- The weekly goal changes mid-week: the new goal applies to this week and later, never to past weeks.
- The phone's clock or time zone changes: weeks are worked out from each cook's stored time in the current zone.

### Out of scope
- Badges and achievements (Phase 5)

---

## 1.6 Finish screen

**Size:** M · **Depends on:** 1.4, 1.5
**Design:** [Figma › Cook — Done](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=72-184)

### Description
The reward. The dish photo in a green ring, "Dish done!" and a short encouraging line, three stats (XP earned, cook time, week streak), the week's cooks as Monday-to-Sunday dots with "This week: N of 4 cooks", and "How did it turn out?" with Overdone, Pretty good and Nailed it. Continue goes back to the Path with the next lesson unlocked.

### Acceptance Criteria
- [ ] The cook is saved when this screen opens, with its time taken from Start cooking to Finish
- [ ] XP earned, cook time and the week streak show the real values for this cook
- [ ] The week row shows a flame on each day with a cook, highlights today, and says "This week: N of goal cooks"
- [ ] Choosing a rating saves it with the cook; it can be changed until Continue
- [ ] Continue returns to the Path, where the next lesson is now current
- [ ] Back does the same as Continue; it never returns to cook mode
- [ ] Tests cover: the cook is saved once, the stats, the week row, the rating is saved, Continue updates the Path

### Edge cases
- The rating is optional; leaving without one saves the cook without a rating.
- This cook hits the weekly goal: the encouraging line says so ("4 of 4, goal done this week").
- A cook over an hour shows as "1h 05m".

### Out of scope
- Sharing the result

---

## 1.7 End-to-end journey

**Size:** S · **Depends on:** 1.6

### Description
Lock the core loop in with a Maestro flow that runs on a real phone: open the app, start the next lesson, go through every step, finish with a rating, and see the next lesson unlocked with more XP.

### Acceptance Criteria
- [ ] A flow goes Path → intro → every cook step → finish → Path, and checks the XP went up and the next node is current
- [ ] A second flow checks a locked node can't be opened
- [ ] Both flows pass on the test phone from a fresh install
- [ ] The flows are tagged so they can run alone

### Out of scope
- Instrumented Compose tests (added per screen as needed)
