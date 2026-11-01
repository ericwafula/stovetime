# Stovetime

**Recipes & timers for home cooks.** Know exactly when to flip, stir and rest.

Stovetime answers "how long do I cook this?" with cooking guides and timers that keep running while you cook several things at once, then grows into a recipe app with cook mode, a shopping list and your own cookbook.

## Requirements

- Android Studio, latest stable
- JDK 21. Android Studio's bundled JDK works, and Gradle downloads one if it can't find it.
- An emulator or a device on Android 10 (API 29) or later

## Getting started

Clone the repo:

```bash
git clone https://github.com/ericwafula/stovetime.git
cd stovetime
```

Then either:

- **Android Studio:** open the `stovetime` folder, let Gradle sync, pick an emulator or device and press Run.
- **Command line:** with an emulator running or a device plugged in:

  ```bash
  ./gradlew installDebug
  ```

To check everything builds without installing:

```bash
./gradlew assembleDebug
```

## Level 3: with skills and agents

This branch has the finished Claude Code setup in `.ai/`, everything built live in the workshop. Link it once, then start Claude Code:

```bash
.ai/setup.sh
claude
```

Claude Code starts as the `orchestrator` agent and follows the skills in `.ai/skills/`. It asks to enable the Figma and Mobile MCP servers from `.mcp.json` the first time.

## Tools for Level 3

Install these before the Level 3 steps. Everything else comes with the repo.

| Tool | Used for | Setup |
|---|---|---|
| [Claude Code](https://docs.claude.com/en/docs/claude-code/overview) | Everything | `curl -fsSL https://claude.ai/install.sh \| bash`, then run `claude` and sign in |
| [GitHub CLI](https://cli.github.com) | Issues, branches and PRs | `brew install gh`, then `gh auth login` |
| [Maestro](https://maestro.mobile.dev) | End-to-end flows in `.maestro/` | Needs Java 17+. `curl -fsSL "https://get.maestro.mobile.dev" \| bash` |
| [Figma MCP](https://developers.figma.com/docs/figma-mcp-server/) | Reading the design straight from Figma | A Figma account. See below |
| [Mobile MCP](https://github.com/mobile-next/mobile-mcp) | Letting Claude tap through the app on a device | Node.js 20+ and `adb`. See below |
| [uv](https://docs.astral.sh/uv/) | Running code-review-graph and memsearch (they need Python 3.10+) | `brew install uv`, then `uv tool update-shell` and open a new terminal |
| [context-mode](https://github.com/mksglu/context-mode) | Keeping big command output out of context | Comes with the repo: `.claude/settings.json` enables the plugin. Approve it on first run |
| [memsearch](https://github.com/zilliztech/memsearch) | Memory across sessions | Comes with the repo, the same way. Memories go to `.memsearch/` |
| [code-review-graph](https://github.com/tirth8205/code-review-graph) | A map of the code, so Claude opens fewer files | `uv tool install code-review-graph`, then `code-review-graph install --platform claude-code --no-instructions` and `code-review-graph build` |
| [RTK](https://github.com/rtk-ai/rtk) | Shorter command output | Optional, see its README |
| [ctxline](https://github.com/ericwafula/ctxline) | A status line with context used and the 5-hour and weekly limits | Optional. See below |

### Figma MCP

The server is added in step 1 on `workshop/level-3-start`, and is already in `.mcp.json` on `workshop/level-3-complete`.

1. Start `claude` in the project and approve the `figma` server when asked.
2. Run `/mcp`, pick `figma` and choose **Authenticate**. Sign in to Figma in the browser that opens.
3. Check it works: ask Claude to describe the frame at https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=70-98

### Mobile MCP

1. Install Node.js 20 or later (`node -v` to check). The server runs through `npx`, so there's nothing else to install.
2. Make sure `adb` from the Android SDK platform-tools is on your `PATH` (`adb version`).
3. Start an emulator from Android Studio's Device Manager, or plug in a phone with USB debugging on. `adb devices` should list it.
4. Start `claude`, approve the `mobile` server, and ask: "List the available devices."

### ctxline

A status line at the bottom of Claude Code:

```text
Opus 5.5 · stovetime ⑂ main · ctx 186k (19%) · 5h 8%(3h) · wk 28%(2d) · $8.30
```

`ctx` is how full the context window is. `5h` and `wk` are how much of your 5-hour and weekly limits you've used, with the time until each resets. The limits show on Pro, Max and Ultra plans. It runs on your machine, so it uses no tokens.

1. Install it with Homebrew, uv or Node, whichever you prefer:

   ```bash
   brew install ericwafula/tap/ctxline
   ```

   ```bash
   uv tool install git+https://github.com/ericwafula/ctxline
   ```

   ```bash
   npm install -g github:ericwafula/ctxline
   ```

2. Make it your status line. This adds it to `~/.claude/settings.json` and saves any status line you already had:

   ```bash
   ctxline install
   ```

3. Start `claude`. The line appears at the bottom and fills in after the first reply.

To go back to your old status line, run `ctxline uninstall`.

## Project layout

- `app/` — the Android app
- `build-logic/` — Gradle convention plugins (`stovetime.*`)
- `.ai/` — skills, agents and the roadmap used to build the app
- `.maestro/` — end-to-end flows

## License

To be decided.
