# Stovetime

**Recipes & timers for home cooks.** Know exactly when to flip, stir and rest.

Stovetime answers "how long do I cook this?" with cooking guides and timers that keep running while you cook several things at once, then grows into a recipe app with cook mode, a shopping list and your own cookbook.

## Status

Early setup. The plan lives in [`.ai/roadmap/ROADMAP.md`](.ai/roadmap/ROADMAP.md), broken into steps with acceptance criteria in [`.ai/roadmap/backlog/`](.ai/roadmap/backlog/README.md).

## Build and run

Requirements: Android Studio (latest stable) and JDK 17.

```bash
./gradlew assembleDebug
```

## Project layout

- `app/` — the Android app
- `build-logic/` — Gradle convention plugins (`stovetime.*`)
- `.ai/` — the plan, plus the AI skills and agents used to build this app. Unlike many projects, this folder is committed on purpose, so you can copy the setup.

## License

To be decided.
