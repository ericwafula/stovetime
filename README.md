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

## Level 3: build the setup

This branch is the starting point for Level 3. Each step is built live. Skills and agents go in `.ai/skills/` and `.ai/agents/`; run `.ai/setup.sh` after adding one so Claude Code can find it.

- [ ] **0. Check your setup.** Start `claude` in the project and approve the plugins and MCP servers it asks about. Then:

  - `/mcp` lists `code-review-graph` and `context-mode` as connected.
  - `/context-mode:ctx-doctor` shows every check as `[x]`.
  - `/context` gives your starting token count. Note it down.

- [ ] **1. Connect Figma and create a skill.** Add the Figma MCP server, then `/mcp` → `figma` → **Authenticate**:

  ```bash
  claude mcp add --transport http --scope project figma https://mcp.figma.com/mcp
  ```

  Write the skill for how we build screens:

  ```text
  Create a skill in .ai/skills/compose-screen for how we build screens: Route → Screen → Content, the ViewModel through Koin, previews, strings in strings.xml, and only StovetimeTheme and the Stove* components from :presentation:design, never Material. Then run .ai/setup.sh.
  ```

  Then build the screen from its Figma frame:

  ```text
  Build this screen in Jetpack Compose, and add a ViewModel to handle its state: https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=70-98
  ```

- [ ] **2. Create a subagent:**

  ```text
  Create a code-reviewer subagent in .ai/agents/code-reviewer.md: read-only (Read, Grep, Glob), model sonnet, reviews changed files against our conventions and reports only issues it's at least 80% sure of. Then run .ai/setup.sh.
  ```

  ```text
  Review my changes.
  ```

  Compare `/context` before and after: the main session barely grows.

- [ ] **3. Manage context.** Run `/context`, then `/compact keep the decisions about the Path screen`, then `/clear`. Then try the tools that keep context small:

  | Tool | Try | What to look for |
  |---|---|---|
  | context-mode | `Run the Gradle build and tell me if it passed`, then `/context-mode:ctx-stats` | How much output stayed out of context |
  | code-review-graph | `What depends on StoveButton?` | It answers from the graph instead of opening files |
  | memsearch | In a new session: `What did we build last time?` | It recalls the earlier session from `.memsearch/` |
  | RTK | `git status` with and without RTK | Shorter command output |
  | ctxline | Watch the status line during `/compact` | The `ctx` figure drops, and the `5h` and `wk` limits stay in view |

- [ ] **4. Control cost:** run the same small task (for example `Add a preview for the empty state`) on low effort, then on high effort (`/effort`), and compare.
- [ ] **5. Plan:**

  ```text
  Users want to time poached eggs too. Turn that into a roadmap step with acceptance criteria in our backlog format.
  ```

- [ ] **6. Verify:** run the checks, add the Mobile MCP, and let Claude check the screen on a device:

  ```bash
  claude mcp add --scope project mobile -- npx -y @mobilenext/mobile-mcp@latest
  ```

  ```text
  Install the app on my phone and check the Path screen matches the Figma frame. Then add a Maestro flow for it.
  ```

- [ ] **7. See it all together:** `git checkout workshop/level-3-complete`

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
   brew install ericwafula/tools/ctxline
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
- `.ai/` — the roadmap, and the skills and agents you add

## License

To be decided.
