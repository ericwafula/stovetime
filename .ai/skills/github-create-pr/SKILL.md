---
name: github-create-pr
description: Open a draft pull request for a Stovetime branch against main with gh — commit-convention title, plain-language Description/Changes/How to test/Screenshots body with Closes #N, no AI attribution, and no duplicate PRs.
when_to_use: "open a PR", "draft a PR", or the github router's ship-it flow.
---

# Skill: github-create-pr

## Pre-flight (refuse on failure)

1. `gh auth status` must be logged in.
2. `git remote get-url origin` must exist and be on github.com. If not, ask for the repo URL.
3. The current branch must not be `main`.
4. `gh pr view --json url,state`: if a PR already exists for this branch, print its URL and stop.
5. Upstream: if `git rev-parse --abbrev-ref @{u}` fails, confirm with the user, then `git push -u origin <branch>`.

## Title

Same format as the commit convention: `<type>/<scope>: <description>`, 72 characters or fewer. Draft it from `git log main..HEAD --pretty=%s` and confirm.

## Body

Write it for a reviewer with no context, in plain sentences. Describe only what this branch changes: no future work and no selling.

```markdown
## Description

<2–4 sentences: the situation, what this changes, why>

## Changes

- <one line per meaningful change, by layer: datasources / data / domain / feature / design / app / build>

## How to test

- Run the `checks` gate: `./gradlew ktlintFormat build testDebugUnitTest ktlintCheck`
- <manual steps: open Path → tap the first recipe → step through cook mode → see XP on the finish screen …>

## Screenshots

| Before | After |
|---|---|
| TODO | TODO |

Closes #<n>
```

Ask for the issue number if there is one; drop the `Closes` line only when the user confirms there's no issue. Drop the Screenshots section for non-UI changes. Keep the TODOs for UI changes and remind the user to fill them in. If `.github/pull_request_template.md` appears later, use it instead.

## Open

```bash
gh pr create --draft --base main --title "<title>" --body-file <tmpfile>
```

Write the body with `mktemp`. Add `--reviewer` only for handles the user names, since there's no CODEOWNERS yet. Retry once after pushing if `gh` complains about the upstream, and don't loop.

## Report

```text
Draft PR opened: <url>
  <title>   base: main ← <branch>
  Screenshots: TODO (fill in before marking ready)
```

## Refuse

Non-draft creation, PRs from `main`, a second PR for the same branch, marking a PR ready, and any AI attribution in the title or body.
