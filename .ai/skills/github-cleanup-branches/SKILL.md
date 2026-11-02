---
name: github-cleanup-branches
description: Delete Stovetime branches that are merged into main or whose PRs are merged/closed — locally and on origin. Runs right after every merge, not only on request. Never touches main or the current branch.
when_to_use: Right after merging a branch into main; "delete merged branches", "clean up branches", "tidy up git".
---

# Skill: github-cleanup-branches

Merged branches don't stay around. GitHub's "automatically delete head branches" setting, if it's on, only fires for PRs merged on GitHub. After a local merge that's pushed, delete the branch here, locally and on `origin`, as the last step of the merge.

## Build the plan

```bash
git fetch --prune origin
for b in $(git branch --format='%(refname:short)'); do
  case "$b" in main) continue ;; esac
  merged=$(git merge-base --is-ancestor "$b" main && echo main || echo no)
  upstream=$(git rev-parse --abbrev-ref "$b@{u}" 2>/dev/null || echo none)
  pr=$(gh pr list --state all --head "$b" --json state -q '.[0].state' 2>/dev/null)
  echo "$b | merged:$merged | upstream:$upstream | pr:${pr:-none}"
done
```

A branch is a candidate if it's merged into `main`, its PR is `MERGED`/`CLOSED`, or its upstream is gone. Keep it if it's `main`, currently checked out (in this or any other worktree — see `git worktree list`), has unpushed commits, or is unmerged with no PR.

When `merge-base` says "no" but the work may have landed under different hashes (squash, rebase, cherry-pick), check with `git cherry -v main <branch>`: lines starting `-` are already in. If every commit is `-`, or the branch's final tree matches a commit in `main`, it's merged.

When this runs as the last step of a merge the user asked for, delete without asking again. When it's a general clean-up, show both lists (delete / keep with reason) and confirm first.

## Delete

```bash
git push origin --delete <branch>   # if it exists on origin
git branch -d <branch>
git fetch --prune origin
```

Use `git branch -d` only. If git refuses ("not fully merged") but the `git cherry` check above proves the work is in, say so and use `-D`. Otherwise skip the branch and report it.

## Never

Delete `main`, the current branch, a branch checked out in another worktree, or a branch with unmerged work.
