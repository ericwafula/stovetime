---
name: github-sync-base
description: Bring the current Stovetime branch up to date with origin/main by fast-forward or rebase — clean tree required, no auto-resolving conflicts, and a force-push only when the user explicitly says so.
when_to_use: "sync with main", "rebase on main", "my branch is behind", or before opening a PR when the branch has fallen behind.
---

# Skill: github-sync-base

The base is `main` unless the user names another branch.

## Pre-flight

1. The current branch must not be `main`.
2. `git status --porcelain` must be empty. If it isn't, ask the user to commit first. Don't auto-stash.
3. Run `git fetch origin main`.

## Decide

```bash
behind=$(git rev-list --count HEAD..origin/main)
ahead=$(git rev-list --count origin/main..HEAD)
pushed=$(git rev-parse --abbrev-ref @{u} 2>/dev/null || echo none)
```

- `behind == 0`: already up to date, so stop.
- `ahead == 0`: `git merge --ff-only origin/main`.
- Otherwise run `git rebase origin/main`. Tell the user "N behind, M ahead — rebasing M commits" first. If the branch is already pushed, warn that a rebase will need a force-push, which the house rules forbid unless the user says so. Offer `git merge origin/main` instead and let them choose.

## Conflicts

List the conflicted files and stop. Don't pick sides. The user resolves, then runs `git rebase --continue` (or `git merge --continue`) or aborts.

## Push

A merge pushes normally after confirmation. A rebased branch that was already pushed has new SHAs: push it only if the user explicitly asks, and then only with `git push --force-with-lease origin <branch>`. Never a bare `--force`.

## After

Run the `checks` skill, since rebased or merged code can break even when Git reported no conflicts.

Report: `Synced <branch> onto origin/main — was <behind> behind; method: ff/rebase/merge; pushed: yes/no`.
