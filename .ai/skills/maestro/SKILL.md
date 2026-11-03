---
name: maestro
description: Write and run Maestro end-to-end flows for Stovetime — black-box YAML flows in .maestro/flows that drive the installed debug app on an emulator or device, with tag-based runs, selector, permission, wait and assertion rules, and reading failures.
when_to_use: User asks for a Maestro flow, an end-to-end/journey test from the outside, to "run maestro", or to check a journey works on the phone. For in-process Compose tests with fakes, use generate-e2e-test; for checking a screen by hand on the emulator, use the Mobile MCP.
---

# Skill: maestro

Maestro covers whole user journeys from outside the app (path → recipe → cook mode → finish screen). Compose instrumented tests (`generate-e2e-test`) cover single screens with Koin fakes. Use Maestro for the few journeys that must never break, not for every screen state.

## Setup and running

Maestro needs Java 17+. There are no helper scripts; call the CLI directly.

```bash
curl -fsSL "https://get.maestro.mobile.dev" | bash      # once
adb devices                                            # an emulator or device must be listed
./gradlew :app:installDebug                            # install the build you want to test

export MAESTRO_CLI_NO_ANALYTICS=1
maestro test .maestro/flows/smoke                                       # one folder
maestro test .maestro/flows/cook/finishes_first_recipe.yaml             # one flow
maestro test --include-tags=cook,smoke .maestro                         # flows tagged with any of them
maestro --device <serial> test .maestro/flows/smoke                     # pick a device when several are connected
maestro test --format junit --output build/maestro/report.xml --debug-output build/maestro/debug .maestro
```

Running the `.maestro` folder (and so `--include-tags` across folders) needs a workspace config, because Maestro only picks up top-level flows by default. If `.maestro/config.yaml` doesn't exist yet, ask the user before adding one:

```yaml
flows:
  - "flows/**"
```

Until then, run folders or files directly.

Only one process can drive the phone's UI automation at a time. If the Mobile MCP was used on the device, its `com.mobilenext.mobilecli.DeviceServer` keeps it for 30 minutes, and Maestro fails with "driver did not start up in time" or "UiAutomationService already registered". Stop it first; the MCP starts it again the next time it needs it:

```bash
adb shell 'kill $(pidof app_process)'   # or kill the PID shown by: adb shell ps -A | grep DeviceServer
```

## What to run

Test what you changed and what depends on it, not the whole app.

- **Feature work:** `--include-tags=<the features you touched>,smoke`. Because every flow is tagged with each feature it relies on, this also runs flows in other folders that go through your feature. For example, `--include-tags=timers` also runs a `cook` flow that starts a step timer. `smoke` proves the app still launches.
- **Changing navigation, the bottom bar or app start-up:** add `navigation` to the tags. Smoke covers the rest.
- **Before merging a large change into `main`:** run every flow (`maestro test .maestro`).
- **No flow carries your feature's tag yet?** Smoke still runs. If the change is a journey that must never break, write a flow for it (see below).

On a failure, read `build/maestro/debug/` (screenshots, the screen hierarchy JSON and the command log) for the visible texts before guessing; the app's own error message is usually there.

To test an unmerged branch on a device, build it in a temporary worktree (`git worktree add --detach <dir> origin/<branch>`, copy `local.properties`, `./gradlew :app:installDebug` from there), then run Maestro from the main checkout. Remove the worktree afterwards.

## Writing a flow

One journey per file in `.maestro/flows/<area>/<what_it_proves>.yaml`.

```yaml
appId: com.ericwathome.stovetime.start
name: A new cook finishes the first recipe on the path and sees the XP they earned
tags:
  - cook      # the feature it lives under (its folder)
  - path      # every other feature it goes through
  - finish
---
- launchApp:
    clearState: true
    permissions:
      notifications: allow
- tapOn: "Path"
- extendedWaitUntil:
    visible:
      id: "path_node_0"
    timeout: 10000
- tapOn:
    id: "path_node_0"
# …
```

On `workshop/level-3-start` the app installs as "Stovetime Start", so `appId` is `com.ericwathome.stovetime.start` (on `workshop/level-3-complete` it's `com.ericwathome.stovetime`).

Rules:

- **Start clean:** `launchApp` with `clearState: true`, so a flow never depends on another flow or on old progress in Room.
- **Permissions at launch** (`permissions: { notifications: allow }` or `deny`). Don't tap system permission dialogs: their wording differs between phone makers.
- **Select by visible text or test tag** (`id:` matches a Compose `Modifier.testTag`, exposed via `testTagsAsResourceId` at the screen root — add that when the first flow needs `id:`). Never by coordinates or by index. Text selectors are regexes: escape `.`, `?`, `+`, `(`.
- **Wait on outcomes, not time:** `extendedWaitUntil` for network-loaded recipes, never `waitForAnimationToEnd` loops or fixed sleeps. Don't wait out a real step timer; assert it started and is counting.
- **Assert what the user would check:** the content appeared and the error didn't (`assertNotVisible`).
- **Strings:** the app is English only (`res/values/strings.xml`); flows assert the English text on an English-language device.
- **Network:** flows hit the real TheMealDB API. Keep assertions to things that hold for any recipe (a title, a step count, an image), never specific recipe names or values.
- **Tag every feature the flow depends on**, not just its folder. The first tag is the folder. Add another tag for each feature whose behaviour the flow checks or relies on: a cook-mode flow that starts a step timer and checks the Timers tab is tagged `cook` and `timers`. Passing through a screen on the way (tapping the Path tab to reach a recipe) doesn't count; only the features the flow actually uses do. Use the existing tag names (`path`, `recipes`, `cook`, `timers`, `finish`, `me`, `navigation`, `smoke`) and add a new one only for a new feature.
- **When you change a feature, check the tags.** If a flow now goes through a feature it didn't before, add that tag, so `--include-tags` keeps picking it up.

## Reference

`.maestro/flows/smoke/launch_shows_app_name.yaml`, README § "Tools for Level 3".
