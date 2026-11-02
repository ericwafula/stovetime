# Building the app with /goal

How to build Stovetime end to end from this roadmap in an unattended `/goal` run, one phase at a time.

## Before you start

- [ ] Work in this checkout on `workshop/level-3-complete`, up to date with GitHub
- [ ] `.ai/setup.sh` and `code-review-graph build` have run
- [ ] Start `claude` here, then `/mcp` → `figma` → **Authenticate**, and check `figma`, `mobile` and `code-review-graph` are connected
- [ ] The test phone is plugged in and `adb devices` lists it
- [ ] The session is in auto mode (or has an allowlist for Gradle, adb, maestro and git), so it doesn't stop for permission prompts
- [ ] There's room in the weekly limit: one phase is a lot of turns

## Rules for the run

These override "ask before committing" in `CLAUDE.md` and the orchestrator, for this run only:

1. **Order.** Work through the phase's steps in the order its backlog file gives. Skip steps already marked ✅.
2. **Per step.** Read the step, build it with the skills, write its tests, run `checks`, dispatch `code-reviewer` and fix confirmed issues, then check UI steps on the phone with the Mobile MCP against the Figma frame.
3. **Done means done.** Tick each acceptance criterion in the backlog file only when it's met, and the definition of done in `backlog/README.md` holds. Then set the step to ✅ in `ROADMAP.md`.
4. **Commit each step, never push.** One commit per step on `workshop/level-3-complete`, straight on the branch (no feature branches or PRs): `feature/<scope>: <what it adds> (<step id>)`. Only after `checks` is green. Never push, amend or force-push.
5. **When something is unclear,** pick the option that best fits the spec and the design, write it in the roadmap's decision log with the date, and keep going. Only stop for something that can't be decided from the roadmap, the skills or Figma.
6. **When something is blocked** (a test you can't make pass, a missing permission, the phone gone), write it under "Blocked" at the bottom of this file with what you tried, and move on to the next step that doesn't depend on it.
7. **Stay in scope.** Don't build anything a step marks out of scope, and don't start Phase 5.

## Goals, one per phase

Run them in order. Paste one, wait for it to finish, look at the commits, then run the next.

```text
/goal Phase 0 is done: every step in .ai/roadmap/backlog/phase-0-foundations.md is ✅ in ROADMAP.md with all its acceptance criteria ticked, each step is committed on workshop/level-3-complete, and checks are green. Follow .ai/roadmap/BUILD.md.
```

```text
/goal Phase 1 is done: every step in .ai/roadmap/backlog/phase-1-path.md is ✅ in ROADMAP.md with all its acceptance criteria ticked, each step is committed on workshop/level-3-complete, checks are green, and the 1.7 Maestro flows pass on the phone. Follow .ai/roadmap/BUILD.md.
```

```text
/goal Phase 2 is done: every step in .ai/roadmap/backlog/phase-2-timers.md is ✅ in ROADMAP.md with all its acceptance criteria ticked, each step is committed on workshop/level-3-complete, and checks are green. Follow .ai/roadmap/BUILD.md.
```

```text
/goal Phase 3 is done: every step in .ai/roadmap/backlog/phase-3-recipes.md is ✅ in ROADMAP.md with all its acceptance criteria ticked, each step is committed on workshop/level-3-complete, and checks are green. Follow .ai/roadmap/BUILD.md.
```

```text
/goal Phase 4 is done: every step in .ai/roadmap/backlog/phase-4-me.md is ✅ in ROADMAP.md with all its acceptance criteria ticked, each step is committed on workshop/level-3-complete, checks are green, and the 1.7 Maestro flows still pass. Follow .ai/roadmap/BUILD.md.
```

## Blocked

Nothing yet.
