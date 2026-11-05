---
name: workshop
description: Run a live Level 3 workshop step on Stovetime's level-3-start branch — find that step's TODO(workshop N) markers, fill them in with the project's skills, the roadmap spec and Figma, remove each marker once it's done, run the checks, and fall back to the finished file from level-3-complete if a step goes wrong.
when_to_use: "Do workshop step N", "fill in the workshop TODOs", "build the recipe page", "write the skill/agent for step N", or when the user points at a TODO(workshop …) marker. Also "skip step N" or "restore step N", for the fallback.
---

# Skill: workshop

`workshop/level-3-start` is Stovetime with a few pieces hollowed out for the live session. Each gap is marked `TODO(workshop N)` or `TODO(workshop N.n)`, with a one-line note of what goes there. `workshop/level-3-complete` has every piece finished: it's the answer key.

## Find a step's gaps

```bash
grep -rn "TODO(workshop 6" --include="*.kt" --include="*.md" .
```

Leave out the number to list every gap. Markers are the one kind of comment allowed in this repo; each one is removed as soon as its gap is filled.

## The steps

| Step | Marker | Where | What to build | Skills |
|---|---|---|---|---|
| 0 | none | `CLAUDE.md` (missing) | Run `/init`, then trim it and add the house rules: no AI attribution, checks before any commit, commit only when asked, no secrets, no comments | — |
| 1 | `workshop 1` | `.ai/skills/add-repository`, `.ai/skills/new-compose-screen` | Write both skills from their marker notes, following the shape of the other skills in `.ai/skills/`. Run `.ai/setup.sh` afterwards | `architecture` |
| 2 | `workshop 2` | `.ai/agents/code-reviewer.md` | Write the reviewer from its marker note. Run `.ai/setup.sh` afterwards | `review-pr` |
| 3–4 | none | — | Context and cost are shown, not built | — |
| 5 | `workshop 5` | `.ai/roadmap/backlog/phase-3-recipes.md` | Read step 3.3, the recipe page's spec. Then turn "users want to save recipes they like" into a new step in the same format: size, dependencies, description, acceptance criteria, edge cases, out of scope. There's no answer key for this one | — |
| 6.1 | `workshop 6.1` | `LookupMealRemoteDatasource` in `:datasources:network` | The TheMealDB `lookup.php` call | `add-datasource` |
| 6.2 | `workshop 6.2` | `GetRecipeLocalDatasource`, `SaveRecipeLocalDatasource` in `:datasources:local` | Read and save a recipe in Room (the entity and DAO already exist) | `add-datasource` |
| 6.3 | `workshop 6.3` | `GetRecipeRepository` in `:core:data` | Cache first: the phone's copy, else fetch, save and return | `add-repository` |
| 6.4 | `workshop 6.4` | `RecipeViewModel` in `:features:recipes` | Load, retry, tick ingredients, start cooking | `add-viewmodel` |
| 6.5 | `workshop 6.5` | `RecipeScreen` in `:features:recipes` | The page from Figma frame Recipe — Details (`106:103`), with loading and error states | `implement-design`, `new-compose-screen`, `collect-one-time-event` |
| 7 | none | — | Verify: `checks`, the `code-reviewer` agent, the page on the phone with the Mobile MCP, then airplane mode: a recipe opened once still opens offline | `checks`, `generate-unit-test` |

## Doing a step

1. List the step's markers and read each one.
2. For build steps, read the spec (step 3.3 for the recipe page) and use the skills in the table. Keep every existing signature: the rest of the app already calls them.
3. Replace the placeholder body with the real one and delete the marker line.
4. Run `checks`. For step 6, add the tests with `generate-unit-test`: `LookupMealRemoteDatasourceImplTest` (a found meal, `{"meals": null}`, a server error), `GetRecipeRepositoryImplTest` (cache hit, cache miss then save, not found, offline) and `RecipeViewModelTest` (load, error then retry, ingredient ticks, start cooking only once loaded).
5. Say what was filled in, file by file.

Don't touch anything without a marker, and don't commit unless the user asks.

## When a step goes wrong

Take the finished version of a file from the answer key:

```bash
git checkout workshop/level-3-complete -- <path>
```

For the whole recipe page at once:

```bash
git checkout workshop/level-3-complete -- \
  datasources/network/src/main/java/com/ericwathome/stovetime/datasources/network/sources/recipes/LookupMealRemoteDatasource.kt \
  datasources/network/src/test/java/com/ericwathome/stovetime/datasources/network/sources/recipes/LookupMealRemoteDatasourceImplTest.kt \
  datasources/local/src/main/java/com/ericwathome/stovetime/datasources/local/sources/recipes/recipe \
  core/data/src/main/java/com/ericwathome/stovetime/core/data/repo/recipes/GetRecipeRepository.kt \
  core/data/src/test/java/com/ericwathome/stovetime/core/data/repo/recipes/GetRecipeRepositoryImplTest.kt \
  features/recipes/src/main/java/com/ericwathome/stovetime/features/recipes/recipe \
  features/recipes/src/test/java/com/ericwathome/stovetime/features/recipes/recipe/RecipeViewModelTest.kt
```

The same works for `CLAUDE.md`, `.ai/skills/add-repository`, `.ai/skills/new-compose-screen` and `.ai/agents/code-reviewer.md`.
