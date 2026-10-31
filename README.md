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

This branch is the starting point for Level 3. Each step is built live.

- [ ] **1. Connect Figma and create a skill.** Add the Figma MCP server, then write a skill for how we build screens (Route → Screen → Content, with previews):

  ```bash
  claude mcp add --transport http --scope project figma https://mcp.figma.com/mcp
  ```

  Then give Claude the Figma frame instead of the screenshot:

  ```text
  Build this screen in Jetpack Compose, and add a ViewModel to handle its state: https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=24-92
  ```

- [ ] **2. Create a subagent:** a read-only code reviewer on Sonnet.

  ```text
  Review my changes.
  ```

- [ ] **3. Manage context:** run `/context`, then `/compact keep the decisions about the Kitchen screen`, then `/clear`.
- [ ] **4. Control cost:** run the same small task on low effort, then on high effort, and compare.
- [ ] **5. Plan:**

  ```text
  Users want to time poached eggs too. Turn that into a roadmap step with acceptance criteria.
  ```

- [ ] **6. Verify:** run the checks, add a Maestro flow for the Kitchen screen, and add the Mobile MCP to check it on the emulator:

  ```bash
  claude mcp add --scope project mobile -- npx -y @mobilenext/mobile-mcp@latest
  ```

- [ ] **7. See it all together:** `git checkout workshop/level-3-complete`

Skills and agents go in `.ai/skills/` and `.ai/agents/`. Run `.ai/setup.sh` after adding one so Claude Code can find it.

## Tools for Level 3

Install these before the Level 3 steps. Everything else comes with the repo.

| Tool | Used for | Setup |
|---|---|---|
| [Claude Code](https://docs.claude.com/en/docs/claude-code/overview) | Everything | `curl -fsSL https://claude.ai/install.sh \| bash`, then run `claude` and sign in |
| [GitHub CLI](https://cli.github.com) | Issues, branches and PRs (the `github` skill) | `brew install gh`, then `gh auth login` |
| [Maestro](https://maestro.mobile.dev) | End-to-end flows in `.maestro/` | Needs Java 17+. `curl -fsSL "https://get.maestro.mobile.dev" \| bash` |
| [Figma MCP](https://developers.figma.com/docs/figma-mcp-server/) | Reading the design straight from Figma | A Figma account. See below |
| [Mobile MCP](https://github.com/mobile-next/mobile-mcp) | Letting Claude tap through the app on an emulator | Node.js 20+ and `adb`. See below |

### Figma MCP

The server is in `.mcp.json` (on `workshop/level-3-complete`), or added in step 1 on `workshop/level-3-start`.

1. Start `claude` in the project and approve the `figma` server when asked.
2. Run `/mcp`, pick `figma` and choose **Authenticate**. Sign in to Figma in the browser that opens.
3. Check it works: ask Claude to describe the frame at https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=24-92

### Mobile MCP

1. Install Node.js 20 or later (`node -v` to check). The server runs through `npx`, so there's nothing else to install.
2. Make sure `adb` from the Android SDK platform-tools is on your `PATH` (`adb version`).
3. Start an emulator from Android Studio's Device Manager, or plug in a phone with USB debugging on. `adb devices` should list it.
4. Start `claude`, approve the `mobile` server, and ask: "List the available devices."

## Project layout

- `app/` — the Android app
- `build-logic/` — Gradle convention plugins (`stovetime.*`)
- `.ai/` — the roadmap, and the skills and agents you add

## License

To be decided.
