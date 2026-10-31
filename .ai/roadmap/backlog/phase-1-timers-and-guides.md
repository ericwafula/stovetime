# Phase 1 — Timers & cooking guides

**Order:** 1.1 → 1.2 → 1.3 → 1.4 → 1.6 → 1.5 → 1.7 → 1.8 → 1.9 → 1.10

## 1.1 Quick timer

**Size:** M · **Depends on:** 0.1, 0.4
**Design:** [Figma › 1.1 Quick timer — new ticket](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=24-190)

### Description
The fastest way to time something: pick minutes and seconds, optionally name it ("Pasta"), and start.

### Acceptance Criteria
- [ ] Minutes and seconds can be set with large controls, plus shortcuts for 1, 3, 5, 10 and 15 minutes
- [ ] A timer can be named; without a name it's called "Timer 1", "Timer 2" …
- [ ] Starting shows the countdown with pause, +1 min and cancel
- [ ] The last five custom times are offered as one-tap repeats
- [ ] Tests cover: shortcut sets the right time, unnamed timers are numbered, recent times are remembered

---

## 1.2 Several timers at once

**Size:** M · **Depends on:** 1.1
**Design:** [Figma › 1.2 Timers — on the rail](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=24-92)

### Description
Cooking dinner means the rice, the chicken and the sauce at the same time. Show every running timer on one screen, soonest first.

### Acceptance Criteria
- [ ] The Timers tab lists every running timer with its name, time left and progress, soonest first
- [ ] Each timer can be paused, given +1 min or cancelled from the list
- [ ] A timer within the last minute is highlighted, and a finished one stays at the top until dismissed
- [ ] The ongoing notification shows the next timer to finish and how many are running
- [ ] Tests cover: ordering by time left, finished timers stay on top, actions affect only their own timer

---

## 1.3 Cooking guides content

**Size:** M · **Depends on:** 0.3

### Description
Write and bundle the first guides as JSON, following the schema in the roadmap (§6): eggs, chicken, beef and rice. Each guide has options (cut, method, doneness), timed steps and a doneness check.

### Acceptance Criteria
- [ ] Guides for boiled eggs, chicken (breast, thighs, drumsticks, whole), beef (steak by doneness, stew, mince) and rice (white, brown, pilau)
- [ ] Every option has at least one timed step and a doneness check; chicken shows the safe internal temperature
- [ ] Guides load offline from the app and are validated at build time (bad JSON fails the build)
- [ ] Tests cover: every guide parses, every option has a timed step and a doneness check

---

## 1.4 Boiled eggs guide

**Size:** S · **Depends on:** 1.1, 1.3
**Design:** [Figma › 1.4 Boiled eggs guide](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=24-254)

### Description
The most-searched cooking question, answered in one screen: choose soft, jammy, medium or hard and tap start. A good first step to build live.

### Acceptance Criteria
- [ ] The screen shows the four doneness choices with a picture and the time for each
- [ ] Choosing one and tapping Start starts a timer named after the choice ("Jammy eggs")
- [ ] An "extra-large eggs" switch adds a minute to every time
- [ ] The doneness check and the "move to cold water" step are shown
- [ ] Tests cover: each choice starts the right time, the switch adds a minute; Maestro: choose jammy → timer running

---

## 1.5 Chicken and beef guides

**Size:** M · **Depends on:** 1.3, 1.6
**Design:** [Figma › 1.5 Chicken guide](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=26-126) (beef follows the same layout)

### Description
Chicken and beef need more than one timer: sear, flip, roast, rest. Let the cook pick the cut, method and doneness, then run it as a step sequence (1.6).

### Acceptance Criteria
- [ ] The cook chooses cut → method → doneness (beef) in at most three taps
- [ ] The chosen option starts a step sequence with every step's name and time visible
- [ ] Resting time is always its own step
- [ ] The safe-temperature note is shown before starting chicken
- [ ] Tests cover: each choice path starts the right sequence; Maestro: chicken thighs → pan → sequence running

---

## 1.6 Step sequences

**Size:** M · **Depends on:** 0.4, 1.2
**Design:** [Figma › 1.6 Step sequence — cook mode](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=26-187)

### Description
A timer made of steps: "sear 4 min → flip → sear 4 min → rest 5 min". At the end of each step the app alerts and shows what to do next.

### Acceptance Criteria
- [ ] A sequence shows the current step, time left and what comes next
- [ ] At the end of each step, it alerts (sound and vibration) and waits for "Done" or starts the next step automatically, per the step's setting
- [ ] A step can be skipped or given +1 min
- [ ] Sequences appear in the running-timers list like any other timer
- [ ] Tests cover: steps advance in order, waiting steps don't start until confirmed, skipping moves on

---

## 1.7 Alarm screen

**Size:** M · **Depends on:** 0.4
**Design:** [Figma › 1.7 Alarm — full screen](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=26-263) · [1.7b Ongoing notification](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=37-231)

### Description
When a timer ends and the phone is locked or in a pocket, show a full-screen alarm with the timer's name and what to do next.

### Acceptance Criteria
- [ ] A finished timer shows a full-screen alarm over the lock screen with its name and next step
- [ ] Stop and "+1 min" are large and work with a wet thumb (one tap, no swipe)
- [ ] Two timers ending together are shown on the same alarm screen
- [ ] The alarm follows the sound and vibration settings, and stops after two minutes without a response, leaving a notification
- [ ] Tests cover: two simultaneous alarms, +1 min restarts that timer only

---

## 1.8 Alarms that always ring

**Size:** M · **Depends on:** 0.4, 1.7
**Design:** [Figma › 1.7 Alarm — full screen](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=26-263) · [1.7b Ongoing notification](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=37-231)

### Description
0.4 makes timers survive the background on a test device. This step makes them ring on the phones people actually own: Android 14+ permission rules, silent mode and Do Not Disturb, and the aggressive battery savers on budget phones (Tecno, Infinix, Xiaomi, Samsung).

### Acceptance Criteria
- [ ] Alarms are scheduled as exact alarms (`USE_EXACT_ALARM`, since timing is the app's core feature) and fire on time in Doze
- [ ] When full-screen intents aren't allowed (`canUseFullScreenIntent()` is false on Android 14+), a finished timer falls back to a heads-up notification with Stop and +1 min, and Settings explains how to turn full screen back on
- [ ] Alarms play on the alarm stream, so they ring with the phone on silent and through Do Not Disturb (alarm category)
- [ ] The foreground service declares its type for Android 14+, with the reason documented for the Play Console
- [ ] On Android 16, a running timer is a promoted ongoing notification (Live Update) with a status-bar chip and segmented progress, as in 1.7b; older versions show a standard ongoing notification with a progress bar
- [ ] If the phone restricts the app's background work, a one-time card explains how to allow it, with a link to that phone's battery settings
- [ ] Tests cover: fallback to heads-up when full screen is denied, alarm audio attributes use the alarm usage; manual check on one budget device (Tecno or Infinix) with the screen off for 30 minutes

---

## 1.9 Timer states and first run

**Size:** M · **Depends on:** 1.2, 1.7
**Design:** [Figma › 1.9a Rail — empty](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=41-348) · [1.9b Paused, overrun, notifications off](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=41-416) · [1.9c First run](https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=41-514)

### Description
The happy path is designed; the states around it aren't. Cover what the cook sees with nothing running, a paused or overrunning timer, permissions turned off, and the very first launch.

### Acceptance Criteria
- [ ] With no running timers, the rail shows an empty state with a "Fire a timer" action instead of an empty strip
- [ ] A paused timer's ticket says "Paused", shows the time left without counting, and offers Resume
- [ ] A finished timer that isn't stopped keeps counting up on its ticket and alarm ("+0:45 over") until stopped
- [ ] If notifications are off, a banner on the Timers tab says timers won't ring and opens the notification settings
- [ ] The first launch explains "Fire" in one line, then asks for the notification permission when the first timer starts (not at launch)
- [ ] Times like "plated by 19:16" follow the phone's 12/24-hour setting
- [ ] Tests cover: empty state appears and disappears, paused time doesn't change, overrun counts up from zero; Maestro: first run → fire a 1 min timer → permission prompt

---

## 1.10 Kitchen-ready accessibility

**Size:** M · **Depends on:** 1.2, 1.6, 1.7

### Description
The definition of done covers the basics. A timer app used with wet hands, from across the counter, sometimes by ear, needs more.

### Acceptance Criteria
- [ ] Tickets and countdowns stay readable at 200% font scale: tickets grow taller instead of clipping, and countdowns shrink to fit their width
- [ ] TalkBack announces a timer's name and time left when focused, and only announces changes at step changes, the last minute and the finish (never every second)
- [ ] The step tracker says which step is current in words ("Step 2 of 4, Flip, current"), not only by colour
- [ ] Cook mode keeps the screen on while a sequence runs, and works in landscape for a phone propped on the counter
- [ ] Both themes keep every text pair at WCAG AA (4.5:1, or 3:1 for large text), and small text over photos uses the scrim from the design
- [ ] Tests cover: screenshot tests at font scale 2.0 for the rail and cook mode; an accessibility test for the step tracker's content description
