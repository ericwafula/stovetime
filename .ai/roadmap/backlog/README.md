# Backlog

The [roadmap](../ROADMAP.md) broken into **segments** (one per phase) and **steps** (one per roadmap item). Each step is a spec: what the user gets and how we'll know it's done, not how to build it. A step can be copied straight into a GitHub issue.

| Segment | File | Steps | Goal |
|---|---|---|---|
| 0 — Foundations | [phase-0-foundations.md](phase-0-foundations.md) | 5 | Modules, navigation, storage and the TheMealDB client the features build on |
| 1 — The Path | [phase-1-path.md](phase-1-path.md) | 7 | Pick the next lesson, cook it step by step, earn XP and keep the streak |
| 2 — Timers | [phase-2-timers.md](phase-2-timers.md) | 4 | Timers that keep running and ring with the app closed |
| 3 — Recipes | [phase-3-recipes.md](phase-3-recipes.md) | 5 | Sync TheMealDB to the phone, browse it offline and cook any recipe |
| 4 — Me | [phase-4-me.md](phase-4-me.md) | 3 | Progress, history and settings |

Phase 5 (Later) stays at roadmap level until Phases 0–4 ship.

## How to use

- Work top to bottom inside a segment. Each step lists what it **depends on**.
- Step IDs match the roadmap (e.g. `1.2`). Use them in branch names and commits: branch `ew/feature/1.2-path-screen`, commit `feature/path: adds the path screen (1.2)`.
- Tick criteria here as they're met, then update the status in the roadmap.
- The skills say *how* to build each step. A step never names classes, files or libraries.

## Step format

```markdown
## <id> <Title>

**Size:** S | M | L · **Depends on:** <ids or —>
**Design:** <Figma link, or "needs a frame">

### Description
<what the user gets and why, in plain language>

### Acceptance Criteria
- [ ] <one testable, user-visible condition per line>
- [ ] Tests cover: <the cases that prove it>

### Edge cases
- <a situation that would otherwise be decided mid-build, and the answer>

### Out of scope
- <what this step deliberately doesn't do, and where it happens instead>
```

Leave out **Edge cases** or **Out of scope** only when there's genuinely nothing to say.

## Definition of done (applies to every step)

A step is done when all its acceptance criteria are met **and** every item below holds. These aren't repeated in each step:

- [ ] The `checks` skill is green: ktlint, build (including the hardcoded-text check) and unit tests
- [ ] New logic has unit tests; ViewModels are tested through their actions and state
- [ ] Screens go through a ViewModel; no screen reads Room, the network or the timer engine directly
- [ ] Screens show loading, empty and error states where they apply
- [ ] The UI matches its Figma frame and uses only `:presentation:design` tokens and components, never Material
- [ ] All user-facing text is in `strings.xml`
- [ ] Touch targets are at least 48dp, and anything used while cooking works with one tap
- [ ] TalkBack reads every control; timers announce their name and time left
- [ ] The `code-reviewer` agent has no confirmed issues left
- [ ] Checked on a real device or emulator with the Mobile MCP
