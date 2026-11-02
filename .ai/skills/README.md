# Skills

The playbooks Claude Code follows to build Stovetime. Each lives in `.ai/skills/<name>/SKILL.md`; `.ai/setup.sh` links them into `.claude/skills/` so Claude Code finds them.

## Architecture & scaffolding

| Skill | What it does |
|---|---|
| [`architecture`](./architecture/SKILL.md) | Module map, dependency rules, where new code goes. Start here. |
| [`add-feature-module`](./add-feature-module/SKILL.md) | New Gradle module on the `stovetime.*` convention plugins, with settings and app wiring. |
| [`add-datasource`](./add-datasource/SKILL.md) | TheMealDB (Ktor) or Room/DataStore datasource: one `fun interface` per operation, internal Impl, aggregator, Koin. |
| [`add-repository`](./add-repository/SKILL.md) | `:core:data` repository combining remote and local, with mappers and `DataResult`. |
| [`add-usecase`](./add-usecase/SKILL.md) | `:core:domain` use case, only for logic shared by more than one screen (XP, streak). |
| [`add-worker`](./add-worker/SKILL.md) | WorkManager background work. Cook timers are not workers. |
| [`date-helpers`](./date-helpers/SKILL.md) | The one home for date and time helpers: device time zone, calendar-week streaks, timer end times. |

## UI

| Skill | What it does |
|---|---|
| [`add-viewmodel`](./add-viewmodel/SKILL.md) | `MviViewModel<Action, State, Event>` with `ViewState<T>` fields. |
| [`new-compose-screen`](./new-compose-screen/SKILL.md) | `Route` → `Screen` (koinViewModel) → stateless `Content` with previews. |
| [`implement-design`](./implement-design/SKILL.md) | Build UI from the Figma frames with `:presentation:design`, and add or change tokens. |
| [`add-navigation`](./add-navigation/SKILL.md) | Navigation 3 destinations, the `Navigator` and the Path, Recipes, Timers and Me tabs. |
| [`add-launcher`](./add-launcher/SKILL.md) | Bottom sheets as launchers in `:presentation:ui`, plus permission helpers. |
| [`collect-one-time-event`](./collect-one-time-event/SKILL.md) | Collect ViewModel one-off events with `CollectOneTimeEvent`. |

## Testing

| Skill | What it does |
|---|---|
| [`generate-unit-test`](./generate-unit-test/SKILL.md) | JUnit4, Truth, MockK and Turbine tests per layer. |
| [`generate-e2e-test`](./generate-e2e-test/SKILL.md) | Hermetic Compose instrumented tests with robots and test tags. |
| [`maestro`](./maestro/SKILL.md) | Black-box Maestro journeys in `.maestro/flows`. |

## Checks

| Skill | What it does |
|---|---|
| [`check-compile`](./check-compile/SKILL.md) | Fast `compileDebugKotlin` type gate. |
| [`check-lint`](./check-lint/SKILL.md) | `ktlintFormat` then `ktlintCheck`, reading the reports. |
| [`check-build`](./check-build/SKILL.md) | Assemble and diagnose build failures, including hardcoded text. |
| [`checks`](./checks/SKILL.md) | The full gate before any commit. |
| [`review-pr`](./review-pr/SKILL.md) | Convention-based review of a diff, branch or PR. |

## Git & GitHub

| Skill | What it does |
|---|---|
| [`commit-convention`](./commit-convention/SKILL.md) | `type/scope: description` commits, branches and PR titles, with no AI attribution. |
| [`github`](./github/SKILL.md) | Router plus the issue → branch → commit → PR flow. |
| [`github-create-branch`](./github-create-branch/SKILL.md) | Branch from `main` with the naming convention. |
| [`github-create-commit`](./github-create-commit/SKILL.md) | Safe staging and a convention commit after `checks`. |
| [`github-create-pr`](./github-create-pr/SKILL.md) | Draft PR to `main` with a plain-language body. |
| [`github-create-issue`](./github-create-issue/SKILL.md) | Duplicate check plus a structured issue. |
| [`github-review-pr`](./github-review-pr/SKILL.md) | Read-only summary of a PR's comments and checks. |
| [`github-cleanup-branches`](./github-cleanup-branches/SKILL.md) | Delete merged branches safely. |
| [`github-sync-base`](./github-sync-base/SKILL.md) | Bring a branch up to date with `main`. |

## Code graph

From [code-review-graph](https://github.com/tirth8205/code-review-graph) (MIT), copied in so nobody needs to run `code-review-graph install`. They use its MCP server; run `code-review-graph build` once after cloning.

| Skill | What it does |
|---|---|
| [`explore-codebase`](./explore-codebase/SKILL.md) | Find your way around through the graph instead of opening files. |
| [`debug-issue`](./debug-issue/SKILL.md) | Trace a bug through callers and dependencies. |
| [`refactor-safely`](./refactor-safely/SKILL.md) | Check what depends on code before changing it. |
| [`review-changes`](./review-changes/SKILL.md) | Review a diff with risk levels and blast radius from the graph. |

## Writing

| Skill | What it does |
|---|---|
| [`humanizer`](./humanizer/SKILL.md) | Plain UI copy for home cooks, and commit and PR text that sounds human. |

## Adding a skill

1. Create `.ai/skills/<name>/SKILL.md` with front matter `name`, `description` and `when_to_use`.
2. Run `.ai/setup.sh` to link it into `.claude/skills/`.
3. Add it to the table above.
