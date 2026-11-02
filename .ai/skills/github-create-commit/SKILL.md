---
name: github-create-commit
description: Create one Stovetime commit with a commit-convention message after checks pass — never on main, never --no-verify, never amending, never staging secrets or local tooling state, and never with AI attribution.
when_to_use: "commit this", "make a commit", or the github router's ship-it flow after checks.
---

# Skill: github-create-commit

## Pre-flight

1. `git status --porcelain` must not be empty.
2. The current branch must not be `main`. If it is, send the user to `github-create-branch`.
3. `git config user.name` and `user.email` must be set.
4. The `checks` skill must be green for this change. If it hasn't run, run it first.

## Stage

Stage paths explicitly (`git add <paths>`), not `git add -A` blindly. Show `git status --short` first, then stage only what belongs to this change. Never stage:

- `local.properties`, `*.jks`/keystores, `.env*`, API keys of any kind
- `.claude/settings.local.json`, `.claude/skills/`, `.claude/agents/` (symlinks made by `.ai/setup.sh`), `.memsearch/`, `.code-review-graph/` — never `git add -f` them
- IDE noise under `.idea/` unless it's a deliberate shared-config change

`.ai/` and `CLAUDE.md` are tracked: stage them only when this change is about them.

## Message

Follow `commit-convention`: `<type>/<scope>: <description>`, lowercase, present tense, 72 characters or fewer. Add a body only when the why isn't obvious. Show the message and confirm it.

```bash
git commit -m "<subject>"                 # or add -m "<body>"
```

No trailers. If a hook fails, show the output and fix the cause. Never use `--no-verify`.

## Report

```text
Committed <short-sha>  <subject>
  <n> files, +<a>/-<d>
```

## Refuse

Committing on `main`, empty commits, `--amend` (unless the user explicitly asks), `--no-verify`, secrets, local tooling state or any attribution line.
