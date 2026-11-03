---
name: github-create-branch
description: Create a Stovetime working branch from an up-to-date main using the <initials>/<type>/<slug> convention, without silently dropping or carrying uncommitted work.
when_to_use: "start a branch", "new branch for X", or the github router's ship-it flow.
---

# Skill: github-create-branch

## Pre-flight

1. `git status --porcelain`. If it isn't empty, ask whether to commit the changes first or carry them onto the new branch.
2. `git symbolic-ref --short HEAD`. Remember it so the user can return.
3. `git fetch origin main` if `origin` exists. If there's no remote, branch from local `main` and say so.

## Name

`<initials>/<type>/<kebab-slug>`, for example `ew/feature/recipe-search` or `ew/fix/streak-week-boundary`. For a roadmap step, the slug starts with the step id: `ew/feature/1.2-cook-mode`.

- `initials`: the user's (`ew` so far). Ask if anyone else is committing.
- `type`: `feature`, `fix` or `chore`, as defined in `commit-convention`.
- `slug`: four kebab-case words at most, no filler, and no repeat of the type word.

Show the name and confirm it before creating the branch.

## Create

```bash
git switch -c <branch> origin/main   # or plain `main` when there is no remote
```

If `main` is checked out in this worktree and behind, `git pull --ff-only origin main` first. If the branch already exists, stop and report it. Don't switch to it silently.

## Report

```text
Branch created.
  Name:     <branch>
  From:     main @ <short-sha>
  Previous: <previous>   (git switch <previous> to return)
```

## Refuse

- Branching from anything but `main` unless asked.
- Carrying a dirty tree without explicit consent.
- Slugs longer than ~40 characters.
