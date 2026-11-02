---
name: new-compose-screen
description: Create a Compose screen in a Stovetime feature module — public <Screen>Route entry point, internal stateful <Screen>Screen that gets the ViewModel via koinViewModel and collects state with lifecycle, private stateless <Screen>Content(state, onAction), previews in StovetimeTheme for every state, built only from :presentation:design.
when_to_use: User asks for a new screen/page/view, shares a Figma frame or screenshot to build, or asks to split a large composable into the Route → Screen → Content structure.
---

# Skill: new-compose-screen

## Inputs

- `<Screen>`: PascalCase name (e.g. `RecipeIntro`, `CookStep`).
- Target feature module (`:features:path`, `:features:recipes`, `:features:cook`, `:features:timers`, `:features:me`), existing or created with `add-feature-module`.
- The design: a Figma frame, read through the Figma MCP (`implement-design`).

## Files

Package: `com.ericwathome.stovetime.features.<feature>.<screen>` (e.g. `features.recipes.intro`).

1. **`<Screen>ViewModel.kt`** — Action / Event / State / ViewModel. Follow `add-viewmodel`.
2. **`<Screen>Screen.kt`**:

```kotlin
@Composable
fun RecipeIntroRoute(
    recipeId: String,
    onBack: () -> Unit,
    onOpenCookMode: (recipeId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    RecipeIntroScreen(
        onBack = onBack,
        onOpenCookMode = onOpenCookMode,
        modifier = modifier,
        viewModel = koinViewModel(key = "recipe_intro_$recipeId") { parametersOf(recipeId) },
    )
}

@Composable
internal fun RecipeIntroScreen(
    onBack: () -> Unit,
    onOpenCookMode: (recipeId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecipeIntroViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            is RecipeIntroEvent.OpenCookMode -> onOpenCookMode(event.recipeId)
        }
    }

    RecipeIntroContent(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun RecipeIntroContent(
    state: RecipeIntroState,
    onAction: (RecipeIntroAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(StovetimeTheme.colors.canvas)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        when (val recipe = state.recipe) {
            ViewState.Initial, ViewState.Loading -> RecipeIntroLoading()
            is ViewState.Success -> RecipeIntroDetails(recipe = recipe.data, onAction = onAction)
            is ViewState.Error -> RecipeIntroError(onRetry = { onAction(RecipeIntroAction.OnClickRetry) })
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun RecipeIntroContentPreview() {
    StovetimeTheme {
        RecipeIntroContent(
            state = RecipeIntroState(recipe = ViewState.Success(sampleRecipe)),
            onAction = {},
            onBack = {},
        )
    }
}
```

3. **Events** (only if the ViewModel sends any) — collect `viewModel.event` in `<Screen>Screen` with `CollectOneTimeEvent` (see `collect-one-time-event`), never a hand-written `LaunchedEffect`; keep `Content` free of the ViewModel.
4. **Koin** — register the ViewModel in the feature's `di/<Feature>Module.kt` (`add-viewmodel`).
5. **Wiring** — register `<Screen>Route` as a destination with `add-navigation`. Navigation lambdas (`onOpenCookMode`, `onBack`) are parameters of `<Screen>Route`, passed down to `<Screen>Screen`, which calls them in response to ViewModel events. Until the nav host exists, the only screen is rendered directly from `MainActivity`.

## Rules

- Only `Route` is public; `Screen` is `internal`, `Content`, its sections and previews are `private`.
- `Content` is stateless: `state` + `onAction` (+ plain callbacks like `onBack`) + `modifier`, nothing else, so it previews without Koin.
- ViewModels with arguments use `koinViewModel(key = "<screen>_$id") { parametersOf(id) }` in the Route, so two recipes on the back stack get two ViewModels.
- Split big screens into small private composables per section (header with `StatChip`s, the path, the step card, the timer row), each with its own preview.
- Build screens only from `:presentation:design`, following `implement-design`: `StoveText`, `StoveIcon`/`StoveIcons`, `StoveButton`, `StoveCard`, `PathNode`, `StatChip`, `TimerChip`, `Pill`, `StoveProgressBar`, `StoveNavBar`. Colours, type, spacing and shapes come from `StovetimeTheme.colors / .typography / .spacing / .shapes`. Never Material components or `MaterialTheme` in a screen, and never `Color(0xFF…)`, a font or a `dp`/`sp` size picked by hand. If the design needs something the design system doesn't have, add it there first.
- Every loading, empty and error state is drawn from the Figma frame. If the frame doesn't show one, ask instead of inventing it.
- Every user-facing string goes in `res/values/strings.xml` of the feature module. `checkHardcodedText` fails the build on `text`, `title`, `description` or `contentDescription` set to a string literal outside previews. Sample data in previews can be literal.
- Recipe photos come from TheMealDB as URLs. Until an image loader is added, pass `photo = null` to `PathNode` and leave other photo slots out; never draw food art yourself. Local images use `painterResource` with drawables named after what they show (`recipe_jammy_eggs`).
- Touch targets are at least 48 dp; icon-only actions have a `contentDescription` from `strings.xml`.
- Composable function names are PascalCase — ktlint is configured to allow that for `@Composable`.
- Use `collectAsStateWithLifecycle()`, not `collectAsState()`.
- Respect edge-to-edge (`MainActivity` calls `enableEdgeToEdge()`): pad with `WindowInsets.systemBars` (or `statusBars` / `navigationBars` where the design runs under one of them).
- Feature-only helpers go in `features/<feature>/.../helpers/`; anything two features share goes in `:presentation:ui`.
- Finish with the `checks` skill.

## Reference

`MainActivity.kt` (`AppName` and its preview) for theme and string usage today; the design-system previews in `presentation/design/.../components/` for component usage. Once a feature exists, its first `<Screen>Screen.kt` becomes the reference.
