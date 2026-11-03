---
name: github
description: Router for GitHub work in Stovetime via the gh CLI — picks the right sub-skill (create-branch, create-commit, create-pr, create-issue, review-pr, cleanup-branches, sync-base) or runs the full issue → branch → checks → commit → push → draft PR flow in order.
when_to_use: User says "/github", "ship it", "open a PR", "file an issue", "start a branch", "commit this", "what's on PR #n", "clean up branches" or "sync with main".
---

# Skill: github

This skill picks a sub-skill and hands off. It makes no `gh` calls itself.

## Principles

1. **`gh` is the only GitHub interface.** No raw `curl` to the API.
2. **Conventions come from `commit-convention`.** Types, scopes, branch and title formats are defined there, not here.
3. **Ask before anything public.** Issue titles, branch names, pushing and opening PRs are permanent and public. Ask with a short multiple-choice prompt when a title, slug, scope, issue number or reviewer is missing.
4. **Pre-flight before public state.** Every sub-skill refuses when the repo state would produce something wrong.
5. **No AI attribution, ever.** No co-author trailers, "generated with" lines, bot emoji or tool names in commits, PRs, issues, branch names or comments.

## Repo facts

- **Base branch: `main`.** Feature branches come off `main` and every PR is a draft into `main`. `Closes #<issue>` in a PR body closes the issue when it merges. There's no `dev` or `release` branch, no version bump and no app distribution step.
- **No CI yet.** The `checks` skill is the only gate, so it runs before every commit.
- **The remote may not exist yet.** If `git remote get-url origin` fails, stop and ask the user for the repo URL before anything that needs GitHub.
- **Single maintainer for now.** There's no CODEOWNERS file, no issue templates and no PR template. Sub-skills fall back to plain formats until those exist, so re-check for them each run.

## Routing

| Signal | Sub-skill |
|---|---|
| "start a branch", "branch off main for X" | `github-create-branch` |
| "commit this", "wrap up the commit" | `github-create-commit` |
| "open/draft a PR" | `github-create-pr` |
| "file/open an issue" | `github-create-issue` |
| "what's on PR #n", "summarise review comments" | `github-review-pr` |
| "delete merged branches", "tidy up" | `github-cleanup-branches` |
| "sync with main", "rebase on main" | `github-sync-base` |
| "ship it" / issue → branch → commit → PR | the flow below |

A bare "/github" means: ask which operation, three options at most.

## Ship it

Stop at the first refusal.

1. **Take stock:** `git status --short`, current branch, `gh auth status`. Stop if there's nothing to ship.
2. **Issue:** `github-create-issue` (it searches open issues first and offers matches).
3. **Branch:** keep the current one if it already matches the convention; otherwise `github-create-branch`.
4. **Checks:** run the `checks` skill. No green, no commit.
5. **Commit:** `github-create-commit`.
6. **Push and draft PR:** `github-create-pr` (it pushes with `-u` after confirming, and returns the existing PR's URL if there is one).

## After a sub-skill returns

Relay the result in one short paragraph (issue number, branch, short SHA, PR URL) with no padding.

## Never

Amend, force-push, use `--no-verify`, mark a PR ready or add reviewers the user didn't name — unless the user explicitly says so.
