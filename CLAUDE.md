# Stovetime

Android app: a learning path for home cooks, with cook mode, timers, XP and a weekly streak. Before any structural change, read `.ai/skills/architecture/SKILL.md`. The playbooks for building things are in `.ai/skills/`, and the plan is in `.ai/roadmap/`.

## Rules for every session and agent

- No AI attribution anywhere: commits, PRs, issues, branch names, code comments, docs. No co-author trailers, "generated with" lines or tool names.
- Run the `checks` skill and get it green before any commit. Never use `--no-verify`, amend or force-push unless the user says so.
- Commit, push, open PRs or file issues only when the user asks.
- Secrets (`local.properties`, API keys) never go into source, commits or messages.
- Code explains itself: no comments unless the code really can't say it.
