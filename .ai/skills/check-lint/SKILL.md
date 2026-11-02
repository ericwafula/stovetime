---
name: check-lint
description: Format and lint Stovetime with ktlint (org.jlleitschuh.gradle.ktlint, experimental rules on) — ktlintFormat then ktlintCheck, read the JSON reports, and fix what the formatter can't.
when_to_use: User asks to "lint", "format", "fix ktlint", or ktlintCheck failed.
---

# Skill: check-lint

Run from the repo root:

```bash
./gradlew ktlintFormat
./gradlew ktlintCheck
```

Scope to one module with `./gradlew :presentation:design:ktlintCheck`.

## Configuration (don't fight it)

- Applied to **all** projects from the root `build.gradle.kts`, `enableExperimentalRules = true`, JSON reporter.
- `ktlint_standard_package-naming` is disabled, so camelCase package segments are allowed.
- `.editorconfig`: functions annotated `@Composable` may be PascalCase; property-naming is off under `resources/`.
- `/generated/`, `/build/` and `ParserData` paths are excluded.
- Every subproject's `preBuild` already depends on `ktlintFormat`, so a normal build reformats sources — always review the diff after building.

## When ktlintCheck still fails

1. Read `<module>/build/reports/ktlint/**/*.json` for file, line and rule id.
2. Fix by hand; common non-autofixable ones: wildcard imports, `property-naming`, `function-naming`, `max-line-length` in strings, `backing-property-naming`.
3. Don't add `@Suppress("ktlint:…")` or edit `.editorconfig` without asking the user.
4. Re-run `ktlintCheck` until clean and show the final output.
