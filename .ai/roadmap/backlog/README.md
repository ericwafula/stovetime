# Backlog

The [roadmap](../ROADMAP.md) broken into **segments** (one per phase) and **steps** (one per roadmap item). Each step has a description and acceptance criteria and can be copied straight into a GitHub issue.

| Segment | File | Steps | Goal |
|---|---|---|---|
| 0 — Foundations | [phase-0-foundations.md](phase-0-foundations.md) | 5 | The app can host many features, store data and run timers that survive the background |
| 1 — Timers & cooking guides | [phase-1-timers-and-guides.md](phase-1-timers-and-guides.md) | 10 | "How long do I cook this?" answered and timed |
| 2 — Recipes & cook mode | [phase-2-recipes-and-cook-mode.md](phase-2-recipes-and-cook-mode.md) | 6 | Add a recipe and cook it step by step |
| 3 — Shopping list | [phase-3-shopping-list.md](phase-3-shopping-list.md) | 4 | Recipes become one merged list |
| 4 — My cookbook | [phase-4-cookbook.md](phase-4-cookbook.md) | 5 | The app becomes your personal cookbook |

Phase 5 (Later) stays at roadmap level until Phases 0–4 ship.

## How to use

- Work top to bottom inside a segment. Each step lists what it **depends on**.
- Step IDs match the roadmap (e.g. `1.4`). Use them in branch names and commits, e.g. branch `ew/feature/1.4-boiled-eggs` and commit `feature/guides: adds the boiled eggs guide (1.4)`.
- Tick criteria here as they're met, then update the status in the roadmap.
- UI steps carry a **Design:** line pointing at their Figma frame once it exists.

## Definition of done (applies to every step)

These are not repeated in each step:

- [ ] `./gradlew ktlintCheck` and `./gradlew test` pass
- [ ] Screens show loading, empty and error states where they apply
- [ ] Screens go through a ViewModel; no screen reads Room or the timer engine directly
- [ ] A running timer keeps counting through rotation, backgrounding and the app being swiped away
- [ ] Touch targets are at least 48dp, and anything used while cooking works with one tap
- [ ] TalkBack reads every control, and timers announce their name and time left
- [ ] Works offline on a low-end test device (minSdk 29, 2–3 GB RAM)
- [ ] UI matches its Figma frame and uses only design-system tokens and components
