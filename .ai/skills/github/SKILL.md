---
name: github
description: Ship Stovetime work with the gh CLI — issue → branch → checks → commit → push → draft PR, using the type/scope commit convention and no AI attribution.
when_to_use: User says "ship it", "open a PR", "file an issue", "start a branch" or "commit this".
---

# Skill: github

`gh` is the only GitHub interface. Ask the user before anything public: issue titles, branch names, pushing and opening PRs.

## Conventions

- **Type:** `feature`, `fix` or `chore`.
- **Scope:** the area touched, from the paths: `kitchen`, `timers`, `guides`, `ui`, `config`, `docs`.
- **Commit and PR title:** `<type>/<scope>: <description>`, lowercase, present tense: `feature/kitchen: adds the category row`.
- **Branch:** `<initials>/<type>/<slug>`, or `<initials>/<type>/<step-id>-<slug>` for a roadmap step: `ew/feature/1.4-boiled-eggs`.
- **Base branch:** `main`.
- **No AI attribution:** no co-author trailers, "generated with" lines or tool names, anywhere.

## Ship it

1. **Take stock:** `git status --short`, current branch, `gh auth status`. Stop if there's nothing to ship.
2. **Issue:** `gh issue list --state open --search "<keywords>"`. Offer the matches, or file a new one with `gh issue create` (plain-language title, short body with the acceptance criteria).
3. **Branch:** keep the current one if it already matches; otherwise `git switch -c <branch> main`.
4. **Checks:** run the `checks` skill. No green, no commit.
5. **Commit:** stage explicit paths only, never `local.properties` or keystores. One commit with the convention above.
6. **Push:** `git push -u origin <branch>`.
7. **Draft PR:** `gh pr create --draft --base main` with a title in the commit format and a body with **Description**, **Changes**, **How to test** and `Closes #<issue>`. If a PR already exists for the branch, return its URL instead.

Report the issue, branch, commit and PR URL in a few lines.

## Never

Amend, force-push, use `--no-verify`, mark a PR ready or add reviewers the user didn't name.
