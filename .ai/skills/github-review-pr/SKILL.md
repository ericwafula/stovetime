---
name: github-review-pr
description: Read-only summary of a Stovetime pull request — metadata, check status, conversation, inline comments grouped by file with resolved/unresolved state, and suggested next steps. Never comments, approves or merges.
when_to_use: "what's on PR #n", "summarise the review comments", "are the checks green on my PR". For reviewing the code itself, use review-pr.
---

# Skill: github-review-pr

Input: a PR number or URL. Ask if missing, and only infer it from the current branch if the user confirms.

## Fetch (in parallel)

```bash
gh pr view <n> --json number,title,url,state,isDraft,author,baseRefName,headRefName,additions,deletions,changedFiles,reviewDecision,statusCheckRollup
gh pr view <n> --json comments
gh api repos/{owner}/{repo}/pulls/<n>/comments
gh api repos/{owner}/{repo}/pulls/<n>/reviews
```

For thread resolution state, use `gh api graphql` on `reviewThreads { isResolved comments { path line body author { login } } }`.

## Summarise

```text
PR #<n> — <title>
  @<author> · <draft|open|merged|closed> · <base> ← <head>
  +<a>/-<d> across <c> files · Checks: <passing|failing: <check names>|none configured>
  Reviews: <approved by …> <changes requested by …>

Conversation
  - @x: <one-line paraphrase>

Inline, by file
  path/to/File.kt
    L<line>  @x  <paraphrase>  <resolved|unresolved>

Unresolved threads: <n>
```

There's no CI workflow yet, so `statusCheckRollup` is usually empty: report "none configured" rather than "passing". Paraphrase in one line. Quote only when exact wording matters.

## Next steps

Three bullets at most: which thread to answer, the main requested change, or whether to run the `checks` skill locally (the only gate until CI exists).

Never post, approve, request changes, merge or close.
