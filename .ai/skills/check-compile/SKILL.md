---
name: check-compile
description: Fast type-check gate for Stovetime — runs compileDebugKotlin (all modules or one) and reports the first real error with its module. No assembly, lint or tests.
when_to_use: Mid-iteration "does it still compile?", after a refactor or rename, before running tests, or when another skill wants a quick gate.
---

# Skill: check-compile

```bash
./gradlew compileDebugKotlin                 # every module
./gradlew :<module>:compileDebugKotlin       # one module, e.g. :presentation:design
```

It catches type errors, missing imports, cross-module resolution failures (`internal` symbols, a missing `projects.*` dependency), Compose compiler errors and Room/KSP code generation (KSP runs before compilation).

It does **not** catch ktlint violations, hardcoded user-facing text (`checkHardcodedText` runs under `check`), failing tests, resource or manifest-merge problems, or Koin wiring (that's runtime).

## Report

On success: `check-compile ✓ compileDebugKotlin (<duration>)`.

On failure: quote the **first** `e:` line with `file:line`, name the module (`> Task :app:compileDebugKotlin FAILED`) and ignore the cascade that follows. Fix it and re-run.

`check-build` and `checks` are strict supersets, so don't run this in the same gate as them.
