# .ai

The Claude Code setup used to build Stovetime: skills, agents and the product roadmap.

Run this once after cloning, so Claude Code can find the skills and agents:

```bash
.ai/setup.sh
```

It links everything into `.claude/`. Re-run it after adding a skill or agent.

| Folder | What's in it |
|---|---|
| `skills/` | Playbooks for *how* to do something: build a screen, add a ViewModel, write tests, run checks, ship a PR |
| `agents/` | *Who* does a self-contained job: `orchestrator` (the main session), `code-reviewer`, `planner` |
| `roadmap/` | *What* to build and in what order, with acceptance criteria per step |

MCP servers (Figma and Mobile) are in `.mcp.json` at the repo root. Claude Code asks to enable them the first time.
