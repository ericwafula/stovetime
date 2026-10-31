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

## Notes

- `build` also runs `ktlintFormat` and fails on hardcoded user-facing text. Check `git status` afterwards and keep formatting changes in the same commit.
- Test reports: `app/build/reports/tests/testDebugUnitTest/index.html`.
- Only call it green when all four pass. Summarise: what ran, pass or fail, and any files the formatter changed.
