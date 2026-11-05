# Stovetime

**Learn to cook, one dish at a time.**

Stovetime is a learning path for home cooks. Short units of real recipes, cooked one step at a time with timers built in, plus XP and a weekly streak to keep you going. A Recipes tab brings in thousands more from TheMealDB.

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

This branch is the finished app with a few pieces taken out, so you can build them live. Each gap is marked `TODO(workshop N)` and says what goes there; list them all with `grep -rn "TODO(workshop" .`. The `workshop` skill knows every step, and `workshop/level-3-complete` has the finished version of each piece.

Skills and agents live in `.ai/`. `.ai/setup.sh` links them into `.claude/`, which is where Claude Code reads them, so run it after you add or change one.

This branch installs as **Stovetime Start** (with a green START banner on its icon), next to the finished **Stovetime** from `workshop/level-3-complete`, so you can compare them on one phone.

Before you start: `.ai/setup.sh`, then `code-review-graph build`, then `claude`. Approve the plugins and MCP servers it asks about, and in `/mcp` pick `figma` → **Authenticate**. Note your starting `/context` number.

- [ ] **0. Make CLAUDE.md yours.** There's no `CLAUDE.md` yet. Run `/init`, read what it wrote, then:

  ```text
  Trim CLAUDE.md to what every session needs, and add our rules: no AI attribution anywhere, run the checks skill before any commit, commit only when I ask, no secrets in source, and no comments unless the code can't say it.
  ```

- [ ] **1. Skills.** Two skills are empty (`TODO(workshop 1)`): how we write a repository ([`.ai/skills/add-repository/SKILL.md`](.ai/skills/add-repository/SKILL.md)), and how we build a screen ([`.ai/skills/new-compose-screen/SKILL.md`](.ai/skills/new-compose-screen/SKILL.md)).

  ```text
  Do workshop step 1: write the add-repository and new-compose-screen skills from their TODOs, in the same shape as our other skills. Then run .ai/setup.sh.
  ```

- [ ] **2. Agents.** The code reviewer is empty (`TODO(workshop 2)`): [`.ai/agents/code-reviewer.md`](.ai/agents/code-reviewer.md).

  ```text
  Do workshop step 2: write the code-reviewer agent from its TODO. Then run .ai/setup.sh and review the last commit with it.
  ```

  Compare `/context` before and after: the review runs in its own context, so the main session barely grows.

- [ ] **3. See and manage context.** Run `/context`, then `/compact keep the decisions about the skills`, then `/clear`. Then try the tools that keep context small:

  | Tool | Try | What to look for |
  |---|---|---|
  | RTK | `git status` with and without RTK | Shorter command output |
  | context-mode | `Run the Gradle build and tell me if it passed`, then `/context-mode:ctx-stats` | How much output stayed out of context |
  | code-review-graph | `What depends on GetRecipeRepository?` | It answers from the graph instead of opening files |
  | memsearch | In a new session: `What did we do last time?` | It recalls the earlier session from `.memsearch/` |
  | ctxline | Watch the status line during `/compact` | The `ctx` figure drops, and the `5h` and `wk` limits stay in view |

- [ ] **4. Control cost.** Look at the `model:` line in [`.ai/agents/planner.md`](.ai/agents/planner.md) (opus) and [`.ai/agents/code-reviewer.md`](.ai/agents/code-reviewer.md) (sonnet): plan on the big model, run focused jobs on a cheaper one. Then run the same small task (for example `Add a preview for the error state of the Recipes tab`) on low effort, then on high effort (`/effort`), and compare.

- [ ] **5. Plan.** Read step 3.3 in [`.ai/roadmap/backlog/phase-3-recipes.md`](.ai/roadmap/backlog/phase-3-recipes.md): it's the spec for the recipe page you'll build next. Then turn a new request into a spec at the end of that file (`TODO(workshop 5)`):

  ```text
  Users want to save recipes they like. Turn that into a roadmap step with acceptance criteria, edge cases and out of scope, in our backlog format.
  ```

- [ ] **6. Build the recipe page.** Tapping a recipe in the Recipes tab shows a placeholder. Build it bottom-up, one layer at a time (`TODO(workshop 6.1)` to `6.5`):

  | Step | Layer | File |
  |---|---|---|
  | 6.1 | Ask TheMealDB for the recipe | [`LookupMealRemoteDatasource.kt`](datasources/network/src/main/java/com/ericwathome/stovetime/datasources/network/sources/recipes/LookupMealRemoteDatasource.kt) |
  | 6.2 | Read and save it on the phone | [`GetRecipeLocalDatasource.kt`](datasources/local/src/main/java/com/ericwathome/stovetime/datasources/local/sources/recipes/recipe/GetRecipeLocalDatasource.kt), [`SaveRecipeLocalDatasource.kt`](datasources/local/src/main/java/com/ericwathome/stovetime/datasources/local/sources/recipes/recipe/SaveRecipeLocalDatasource.kt) |
  | 6.3 | Phone first, then the network | [`GetRecipeRepository.kt`](core/data/src/main/java/com/ericwathome/stovetime/core/data/repo/recipes/GetRecipeRepository.kt) |
  | 6.4 | The ViewModel | [`RecipeViewModel.kt`](features/recipes/src/main/java/com/ericwathome/stovetime/features/recipes/recipe/RecipeViewModel.kt) |
  | 6.5 | The screen, from Figma | [`RecipeScreen.kt`](features/recipes/src/main/java/com/ericwathome/stovetime/features/recipes/recipe/RecipeScreen.kt) |

  ```text
  Do workshop step 6: build the recipe page from spec 3.3, layer by layer — the TheMealDB lookup, the local read and save, the cache-first repository, the ViewModel, then the screen from https://www.figma.com/design/4hJuAVOMHyuF0hEXxbSvlN?node-id=106-103. Add the tests as you go.
  ```

- [ ] **7. Verify.** Run the checks, then:

  ```text
  Review the recipe page with the code-reviewer, then install the app on my phone and check the recipe page against the Figma frame.
  ```

  Then the offline test: open a recipe, turn on airplane mode, go back and open it again. It still opens.

- [ ] **8. See it all together.** Your changes touch files that are different on the finished branch, so put them aside first or git won't switch:

  ```bash
  git stash -u
  git switch workshop/level-3-complete
  .ai/setup.sh
  ```

  Then open `/plugin` to see the plugins this repo turns on. `git stash pop` on `workshop/level-3-start` brings your work back.

Stuck on a step? `git checkout workshop/level-3-complete -- <file>` puts the finished version in. The `workshop` skill lists the files for each step.

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
| [code-review-graph](https://github.com/tirth8205/code-review-graph) | A map of the code, so Claude opens fewer files | `uv tool install code-review-graph`, then `code-review-graph build`. Skip `code-review-graph install`: its MCP server, hooks and skills are already in the repo, and it would rewrite `.mcp.json` with your own path |
| [RTK](https://github.com/rtk-ai/rtk) | Shorter command output | Optional, see its README |
| [ctxline](https://github.com/ericwafula/ctxline) | A status line with context used and the 5-hour and weekly limits | Optional. See below |

### Figma MCP

The server is already in `.mcp.json` on both Level 3 branches, so it only needs signing in.

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
- `.ai/` — skills, agents and the roadmap used to build the app
- `.maestro/` — end-to-end flows

## License

Apache License 2.0. See [LICENSE](LICENSE).
