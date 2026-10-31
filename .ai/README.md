# .ai

The Claude Code setup used to build Stovetime: skills, agents and the product roadmap.

Run this once after cloning, so Claude Code can find the skills and agents:

```bash
.ai/setup.sh
```

It links everything into `.claude/`. Re-run it after adding a skill or agent.

| Folder | What's in it |
|---|---|
| `skills/` | Playbooks for *how* to do something, like building a screen |
| `agents/` | *Who* does a self-contained job, like a code reviewer |
| `roadmap/` | *What* to build and in what order, with acceptance criteria per step |

On this branch `skills/` and `agents/` start empty. The finished set is on `workshop/level-3-complete`.
