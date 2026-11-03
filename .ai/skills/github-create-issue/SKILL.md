---
name: github-create-issue
description: File a GitHub issue for Stovetime with gh — duplicate search first, a type/scope title matching commit-convention, a short plain-language body (what, acceptance criteria, notes), and labels only if they exist.
when_to_use: "file an issue", "open an issue for X", "track this", or the github router's ship-it flow when no issue exists.
---

# Skill: github-create-issue

## Pre-flight

1. Check `gh auth status` and that `origin` exists on github.com.
2. Search for duplicates with `gh issue list --search "<keywords>" --state open`. Show matches and ask before filing another.
3. Check for templates in `.github/ISSUE_TEMPLATE/`. If any exist, read the matching one and mirror its fields. Otherwise use the plain body below.

## Title

`<type>/<scope>: <description>`, where the type is `feature`, `fix` or `chore` (see `commit-convention`). Example: `feature/cook: adds step timers to cook mode`. For a roadmap step, take the acceptance criteria from `.ai/roadmap/`.

## Body

```markdown
### What

<one or two sentences: the problem or goal, and why it matters to a home cook>

### Acceptance criteria

- [ ] <observable outcome>
- [ ] <observable outcome>

### Notes

<constraints, links, screenshots — or "None">
```

One idea per bullet, plain words and no filler.

## File

Show the title and body and confirm before filing.

```bash
gh issue create --title "<title>" --body-file <tmpfile> [--label "<existing labels>"]
```

Only pass labels that exist (`gh label list`). Don't create labels without asking.

## Report

`Issue #<n> filed: <url>`. When called from the ship-it flow, return just the number.
