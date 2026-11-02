---
name: commit-convention
description: Write branch names, commit messages and PR titles in Stovetime's style (type/scope: lowercase present-tense description), with no AI attribution of any kind.
when_to_use: User asks to commit, create a branch, write a commit message, or open a PR.
---

# Skill: commit-convention

## Commit messages

Format: `<type>/<scope>: <description>`

- `type`: `feature` (user-visible behaviour), `fix` (a bug in existing behaviour) or `chore` (config, refactor, docs, tooling).
- `scope`: one lowercase word for the area, taken from the paths touched: `path`, `recipes`, `cook`, `timers`, `me`, `ui`, `data`, `config`, `docs`.
- `description`: lowercase, third-person present tense starting with a verb — `adds`, `updates`, `removes`, `refactors`, `configures`, `fixes`.

Examples:

```text
feature/path: adds the learning path with locked and unlocked nodes
feature/cook: adds step timers to cook mode
fix/timers: keeps a step timer running after leaving cook mode
chore/data: adds the TheMealDB recipe datasource
chore/config: configures the room convention plugin
```

Class names keep their PascalCase inside the description. Subject only; add a body (blank line, wrapped at 72) only when the why isn't obvious.

## Branches

`<initials>/<type>/<kebab-description>`, or `<initials>/<type>/<step-id>-<slug>` for a roadmap step: `ew/feature/1.2-cook-mode`. Without a step: `ew/feature/recipe-search`, `ew/fix/streak-week-boundary`, `ew/chore/update-agp`. Branch from and open draft PRs against `main`. There are no other long-lived branches and no version bumps. The mechanics live in the `github-*` skills.

## PR titles

Same as the commit format: `feature/recipes: adds recipe search`.

## Hard rules

- **No AI attribution anywhere**: no `Co-Authored-By: Claude …`, no "Generated with …" lines, no 🤖, no tool names in commit messages, PR titles/bodies, issues, branch names, code comments or docs.
- Never stage `local.properties`, keystores, `.env*`, `.claude/settings.local.json`, `.memsearch/` or `.code-review-graph/`. `.ai/` and `CLAUDE.md` are tracked, so commit them only when the change is about them.
- Run the `checks` skill before committing; never commit a red build.
- Commit only when the user asks. Don't push without explicit confirmation. Never amend, force-push or use `--no-verify` unless the user says so.
