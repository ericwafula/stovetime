---
name: checks
description: Run Stovetime's full pre-commit gate locally — ktlintFormat, build, testDebugUnitTest, ktlintCheck — and stop at the first failure.
when_to_use: User asks to "run checks", "verify", "make sure it builds", or before any commit or push. Every other building skill ends here.
---

# Skill: checks

From the repo root, in order. Stop at the first failure and report it; don't paper over it.

```bash
./gradlew ktlintFormat
./gradlew build
./gradlew testDebugUnitTest
./gradlew ktlintCheck
```

Faster loop while iterating on one module:

```bash
./gradlew :<module>:testDebugUnitTest :<module>:ktlintCheck
```

## Notes

- There's no CI workflow yet, so this local gate is the only one. Don't tell the user it mirrors CI.
- `build` also runs `ktlintFormat` (through `preBuild`) and `checkHardcodedText`, which fails on user-facing string literals outside previews (see `check-build`). Check `git status` afterwards and keep formatting changes in the same commit.
- Test reports: `<module>/build/reports/tests/testDebugUnitTest/index.html`.
- Only call it green when all four pass. Summarise: what ran, pass or fail, and any files the formatter changed.
