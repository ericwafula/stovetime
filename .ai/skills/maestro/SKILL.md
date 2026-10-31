---
name: maestro
description: Write and run Maestro end-to-end flows for Stovetime — YAML flows in .maestro/flows that drive the installed debug app on an emulator or device, with selector, wait and assertion rules.
when_to_use: User asks for an end-to-end test, to "run maestro", or to check a journey works on the phone. For checking a screen by hand on the emulator, use the Mobile MCP instead.
---

# Skill: maestro

Maestro tests whole journeys from outside the app. Use it for the few journeys that must never break, not for every screen state.

## Setup and running

```bash
curl -fsSL "https://get.maestro.mobile.dev" | bash   # once
./gradlew :app:installDebug                         # with an emulator running
maestro test .maestro/flows                         # every flow
maestro test .maestro/flows/smoke                   # one folder
```

On a failure, read the screenshot and the view hierarchy Maestro prints before guessing.

## Writing a flow

One journey per file: `.maestro/flows/<area>/<what_it_proves>.yaml`.

```yaml
appId: com.ericwathome.stovetime
name: The Kitchen screen opens with the category row
tags:
  - kitchen
---
- launchApp:
    clearState: true
- assertVisible: "Kitchen"
- assertVisible: "Eggs"
```

## Rules

- **Start clean:** `launchApp` with `clearState: true`, so a flow never depends on another one.
- **Select by visible text or test tag** (`id:` matches `Modifier.testTag`). Never by coordinates or index. Text selectors are regexes: escape `.`, `?`, `+`, `(`.
- **Wait on outcomes, not time:** `extendedWaitUntil`, never fixed sleeps.
- **Assert what the user would check:** the right text appeared, and the error didn't.
- **Grant permissions at launch** (`permissions: { notifications: allow }`), never by tapping system dialogs.

## Reference

`.maestro/flows/smoke/launch_shows_app_name.yaml`
