---
name: add-navigation
description: How Stovetime's Jetpack Navigation 3 setup works and how to add a screen to it — @Serializable Destination keys, TopLevelTab (Path, Recipes, Timers, Me), the Navigator API (selectTab, navigate, replace, backTo, open for timer notifications), lambda-taking NavEntry functions in StovetimeNavHost, and the never-delete-a-key rule.
when_to_use: User adds a screen, asks to navigate between screens or features (path → recipe intro → cook step → cook done), add a detail page, a tab, or back handling. Bottom sheets and app dialogs are not destinations; use add-launcher.
---

# Skill: add-navigation

How navigation works in Stovetime (Jetpack Navigation 3, `libs.bundles.navigation3`), and how to add a screen to it. The code lives in `app/src/main/java/com/ericwathome/stovetime/navigation/`; read it before changing anything. If that package doesn't exist yet, the step that adds the first two screens builds it in this shape.

## The pieces

| Piece | File | What it does |
|---|---|---|
| `Destination` | `Destination.kt` | `@Serializable sealed interface Destination : NavKey`. One key per screen; data classes carry arguments (`RecipeIntro(recipeId)`, `CookStep(recipeId)`, `CookDone(recipeId)`). |
| `TopLevelTab` | `TopLevelTab.kt` | The bottom tabs, in order: Path, Recipes, Timers, Me, each with a `StoveIcons` icon (`Home`, `Book`, `Timer`, `User`) and a `@StringRes` label. `TopLevelTab.destinations` is what the `Navigator` treats as tabs. |
| `Navigator` | `Navigator.kt` | Wraps the back stack (`MutableList<NavKey>`) and the tabs. Path is always the root. |
| `StovetimeNavHost` | `StovetimeNavHost.kt` | `rememberNavBackStack(Destination.Path)` → `Navigator(backStack, TopLevelTab.destinations)` → `NavDisplay` with the saveable-state and ViewModel-store decorators and a crossfade, plus an exhaustive `when` that maps each `Destination` to its entry function. Shows `StoveNavBar` only when `navigator.isOnTab`. |
| Entry functions | `navigation/entry/NavEntry<Feature>.kt` | `fun navigation<Screen>(key, …lambdas): NavEntry<NavKey> = NavEntry(key) { <Screen>Route(…) }`. They take lambdas, never the `Navigator`, so features stay free of navigation. |

```kotlin
fun navigationRecipeIntro(
    key: Destination.RecipeIntro,
    onBack: () -> Unit,
    onOpenCookMode: (recipeId: String) -> Unit,
): NavEntry<NavKey> =
    NavEntry(key) {
        RecipeIntroRoute(
            recipeId = key.recipeId,
            onBack = onBack,
            onOpenCookMode = onOpenCookMode,
        )
    }
```

The tab bar maps tabs to `StoveNavItem`s:

```kotlin
StoveNavBar(
    items = TopLevelTab.entries.map { StoveNavItem(icon = it.icon, label = stringResource(it.label)) },
    selected = TopLevelTab.entries.indexOfFirst { it.destination == navigator.currentTab },
    onSelect = { index -> navigator.selectTab(TopLevelTab.entries[index].destination) },
)
```

### Navigator API

- `selectTab(tab)`: pops to Path, then adds the tab unless it is Path (tabs never stack). Reselecting does nothing.
- `navigate(destination)`: pushes. Use for every non-tab screen: recipe intro, cook mode, a recipe opened from the Recipes tab.
- `replace(destination)`: swaps the top screen. Cook step → cook done uses it, so Back from the finish screen doesn't land on the last step.
- `backTo(destination)`: pops until that screen is on top. "Back to the path" on cook done is `backTo(Destination.Path)`.
- `open(destination)`: for notification taps. Does nothing if that screen is already on top, selects a tab, or pushes anything else on top of whatever is showing.
- `replaceAllWith(destination)`, `backward()` (no-op at the root), `lastOrNull<T>()`.
- `isOnTab`, `currentTab`.

Helpers used by a single flow are private `Navigator.` extensions at the bottom of `StovetimeNavHost.kt` (e.g. `finishCooking(recipeId)`).

### Notifications

Timers keep ringing when the app is in the background, and tapping the notification should open the screen it belongs to. A `NotificationTarget` (`:core:data`) is encoded into the timer notification; `toDestination()` (`app/.../notifications/NotificationDestinations.kt`) maps it to a `Destination` (the cook step for a recipe timer, the Timers tab for a standalone one); `MainActivity` hands it to `StovetimeNavHost`, whose effect calls `navigator.open` and then clears the request. There are no URI deep links. Build these pieces with the step that adds timer notifications, not before.

## Adding a destination

1. Add a key to `Destination` (a `data object`, or a `data class` with its arguments). Arguments are ids, never whole models.
2. In the feature: a public `<Screen>Route(…lambdas)` (see `new-compose-screen`). Navigation intents go Action → Event in the ViewModel and out through the Route's lambdas.
3. Add an entry function in `navigation/entry/NavEntry<Feature>.kt`.
4. Add the `is Destination.<Screen> ->` branch in `StovetimeNavHost`, wiring lambdas to `navigator.navigate(…)` / `replace(…)` / `backward()`.
5. If a timer notification should open it, add a `NotificationTarget` case and its `toDestination()` mapping.
6. Tests: `app/src/test/.../navigation/NavigatorTest.kt` for back-stack behaviour, `NotificationDestinationsTest` for targets, and a Maestro flow for the journey (`maestro`).

Bottom sheets and dialogs are not destinations: use `add-launcher`.

## Rules

- **Never delete or rename a `Destination` key or a `NotificationTarget.encoded` value.** Saved back stacks are restored across process death, and scheduled timer notifications carry the encoded target; removing either can crash or drop the tap. Retire a screen by routing its key somewhere else.
- Features never see the `Navigator`; they get lambdas.
- Every pushed screen has a Back affordance (`StoveIcons.Back`, or `StoveIcons.Close` in cook mode) and Back returns to where the cook came from; at most two taps from a tab.
- Tab changes go through `TopLevelTab`; the tab bar shows only on tab roots, never in cook mode.

## Reference

`StovetimeNavHost.kt`, `Navigator.kt`, `TopLevelTab.kt`, `Destination.kt`, `navigation/entry/`, `NavigatorTest.kt`, `NotificationDestinations.kt`, and `StoveNavBar.kt` in `:presentation:design`.
