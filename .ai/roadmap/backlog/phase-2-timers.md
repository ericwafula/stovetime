# Phase 2 — Timers

**Order:** 2.1 → 2.2 → 2.3 → 2.4

## 2.1 Timer engine

**Size:** L · **Depends on:** 0.4

### Description
The part every timer runs on. Any number of timers at once, each with a name, kept alive by a foreground service with one ongoing notification, and set to ring at the right moment even if the app is closed or the phone is locked.

### Acceptance Criteria
- [ ] Start, pause, resume, add a minute and cancel work for several timers at once
- [ ] Times are based on when a timer should end, so it stays accurate after the app is closed and reopened
- [ ] One ongoing notification lists every running timer while any is running, and goes away when none are
- [ ] A timer that ends rings even with the app swiped away or the screen locked
- [ ] Running timers come back after the app is killed and after the phone restarts
- [ ] The notification permission is asked the first time a timer starts, with a short explanation first
- [ ] Tests cover: two timers end in the right order, pause and resume keep the time left, restore after the app is killed, a timer that ended while the app was dead rings straight away on restore

### Edge cases
- The notification permission is refused: timers still run while the app is open, and a note explains what's missing.
- Exact alarms aren't allowed on the phone: fall back to the closest allowed alarm and say so once.

### Out of scope
- Any screen (2.3, 2.4)

---

## 2.2 Step timers on the engine

**Size:** S · **Depends on:** 2.1, 1.4

### Description
Timers started from cook mode run on the engine, so they keep going and still ring when you leave cook mode, lock the phone or close the app.

### Acceptance Criteria
- [ ] Start timer in cook mode creates an engine timer named after the step ("Jammy eggs · Step 2")
- [ ] Coming back to cook mode shows the timer's real time left
- [ ] Finishing or leaving the cook asks whether to keep or cancel any timers still running
- [ ] Tests cover: the timer's name, coming back shows the real time, leaving with a timer running asks first

### Out of scope
- Changing a step timer's length

---

## 2.3 Alarm

**Size:** M · **Depends on:** 2.1
**Design:** needs a frame

### Description
When a timer ends: sound, vibration and a full-screen alarm over the lock screen (or a heads-up notification when the phone is in use) with the timer's name, Stop and "+1 min".

### Acceptance Criteria
- [ ] The alarm shows over the lock screen with the timer's name
- [ ] Stop silences it and clears the timer
- [ ] "+1 min" silences it and restarts that timer for one minute
- [ ] With the phone unlocked and in use, a heads-up notification with the same actions shows instead
- [ ] An alarm nobody stops keeps ringing for one minute, then stays as a notification
- [ ] Tests cover: Stop, "+1 min", two timers ending together each get their own alarm

### Edge cases
- Do Not Disturb: the alarm follows the phone's alarm settings, like a clock alarm.

### Out of scope
- Choosing the alarm sound

---

## 2.4 Timers tab

**Size:** M · **Depends on:** 2.1, 0.3
**Design:** needs a frame

### Description
Every running timer in one list, with time left, pause, "+1 min" and cancel. A quick timer button: give it a name and a time and start it, for cooking that isn't in a lesson.

### Acceptance Criteria
- [ ] Every running and paused timer shows with its name and time left, soonest first
- [ ] Pause, resume, "+1 min" and cancel work from the list
- [ ] A quick timer can be started with a name (optional) and minutes and seconds
- [ ] Tapping a step timer opens its cook mode at that step
- [ ] Tests cover: the list order, each action, a quick timer with and without a name

### Edge cases
- No timers: an empty state with the quick timer button.
- A quick timer with no name is called "Timer 1", "Timer 2" and so on.

### Out of scope
- Saved timer presets
