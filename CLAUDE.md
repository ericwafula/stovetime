# Stovetime

Android app: a learning path for home cooks, with cook mode, timers, XP and a weekly streak. Offline-first, Kotlin, Jetpack Compose and Koin, with no account and no backend. minSdk 29, target and compile SDK 36, JDK 21 for Gradle, Java 11 bytecode.

Before any structural change, read `.ai/skills/architecture/SKILL.md`. The playbooks for building things are in `.ai/skills/`, and the plan is in `.ai/roadmap/`.

## Rules for every session and agent

- No AI attribution anywhere: commits, PRs, issues, branch names, code comments, docs. No co-author trailers, "generated with" lines or tool names.
- Run the `checks` skill and get it green before any commit. Never use `--no-verify`, amend or force-push unless the user says so.
- Commit, push, open PRs or file issues only when the user asks.
- Secrets (`local.properties`, API keys) never go into source, commits or messages.
- Code explains itself: no comments unless the code really can't say it.

## Commands

```bash
./gradlew assembleDebug                      # build
./gradlew installDebug                       # install on the connected phone or emulator
./gradlew ktlintCheck                        # lint (ktlintFormat already runs before every module's preBuild)
./gradlew testDebugUnitTest                  # all unit tests
./gradlew :features:lessons:testDebugUnitTest --tests "com.ericwathome.stovetime.features.lessons.home.PathViewModelTest"   # one test class
./gradlew check                              # includes checkHardcodedText for Compose modules
maestro test .maestro/flows/path             # end-to-end flows for one area, on the installed debug app
```

The full gate before a commit is the `checks` skill: ktlintFormat, build, testDebugUnitTest, ktlintCheck.

## Architecture

- **Modules:** `:app` (Koin wiring, navigation, `MainActivity`, the alarm, the widget, the weekly reminder), `:features:{lessons,recipes,timers,me}`, `:presentation:design` (design system) and `:presentation:ui` (shared composables, `MviViewModel`, `ViewState`, launchers), `:core:{common,domain,data}`, `:datasources:{local,network}` and `:sdk:timer` (foreground service, exact alarms, notifications). Dependencies point down only; features never depend on each other or on `:datasources:*`. The Path tab lives in `:features:lessons`, because Gradle can't generate an accessor named `path`.
- **Data:** course content is bundled JSON in `datasources/local/src/main/assets/courses/`. Progress (cooks, XP, streaks) is in Room, settings in DataStore. Only the Recipes tab uses the network: TheMealDB over Ktor, synced to Room by WorkManager, so screens read the phone's copy. Data calls return `DataResult`; never let an exception reach a ViewModel, and always rethrow `CancellationException`.
- **Convention plugins** live in the included build `build-logic/conventions`: `stovetime.android.application[.compose]`, `stovetime.android.library[.compose]`, `stovetime.android.sdk`, `stovetime.jvm.library`. A module's `build.gradle.kts` is normally the plugin alias plus `namespace` and dependencies. The plugins already add Koin, Timber, coroutines, kotlinx-datetime and the test stack (JUnit4, Truth, MockK, Turbine, coroutines-test). Versions live only in `gradle/libs.versions.toml`, and modules are referenced through type-safe accessors (`projects.presentation.design`).
- **Hardcoded-text check:** the `*.compose` plugins register `checkHardcodedText` (`build-logic/.../helpers/HardcodedText.kt`). It fails on string literals passed to `text`, `title`, `description`, `contentDescription`, `placeholder` and similar outside `@Preview` functions. User-facing text comes from `strings.xml`.
- **Design system:** screens use `StovetimeTheme` (`colors`, `typography`, `spacing`, `shapes`) and the `Stove*` components from `:presentation:design`, never Material components, `MaterialTheme` or raw colours and sizes. A composable outside `StovetimeTheme { }` throws. Light and dark themes both exist.
- **Screens and DI:** every screen is `Route` → `Screen` (ViewModel through `koinViewModel`) → stateless `Content`, and goes through a ViewModel; no screen reads Room, the network or the timer engine directly. Koin starts in `StovetimeApplication`; each module exposes its own Koin module and `AppModule` includes them. Running timers survive rotation, backgrounding and the app being swiped away.

## Product and process

- `.ai/roadmap/ROADMAP.md` is the product roadmap: vision, phases, XP and streak rules, decision log and Figma links. `.ai/roadmap/backlog/phase-*.md` holds each step as a spec (acceptance criteria, edge cases, out of scope), and `backlog/README.md` has the format and the definition of done. Tick criteria and update the roadmap status as work lands, and log decisions with the date.
- Designs are in Figma file `4hJuAVOMHyuF0hEXxbSvlN`, page "v4 — Home cook". Each UI step has a **Design:** line pointing at its frame. Read frames with the Figma MCP through the `implement-design` skill; UI must match the frame and use only design-system tokens.
- Branches and commits use the step ID: branch `ew/feature/1.2-path-screen`, commit `feature/path: adds the path screen (1.2)`. Subjects follow `type/scope: <present-tense verb> …` (see the `commit-convention` skill).
- UX from the definition of done: touch targets of at least 48dp, one-tap actions while cooking, TalkBack labels on every control (timers announce their name and time left), and loading, empty and error states.
- `.ai/roadmap/BUILD.md` has the rules and `/goal` prompts for building a phase unattended.

## Claude Code setup

- Skills and agents are written in `.ai/skills/<name>/` and `.ai/agents/<name>.md`, then linked into `.claude/` by `.ai/setup.sh`. Re-run it after adding one, and don't create them directly in `.claude/`. The `orchestrator` agent is the main session; it hands focused jobs to `code-reviewer` and `planner`.
- `.claude/settings.json` turns off attribution, enables the context-mode and memsearch plugins, and runs hooks that refresh the code-review-graph after every edit. `.mcp.json` registers the `figma`, `mobile` and `code-review-graph` MCP servers. Run `code-review-graph build` once after cloning, and don't run `code-review-graph install`: it would rewrite `.mcp.json` with a local path.
