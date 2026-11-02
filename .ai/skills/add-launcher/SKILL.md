---
name: add-launcher
description: Add or change a bottom-sheet launcher in Stovetime's :presentation:ui — remember[Name]Launcher returning a [Name]Launcher with launch(), a private [Name]LauncherContent, local visibility only, state and callbacks from the host, built on StoveBottomSheet. Also covers activity-result helpers (the notification permission request, system settings screens), which follow a different shape.
when_to_use: Any bottom sheet or app dialog (streak details, ingredients during cook mode, leave cook mode, add a timer, confirmations); a screen needs to start an Activity for a result or ask for a permission; reviewing code that names something *Sheet / *BottomSheet / *Dialog in a feature.
---

# Skill: add-launcher

## Persona

Approach this as a world-class Android engineer on the Jetpack Compose team. Recomposition and skippability, stability (`@Stable`/`@Immutable`, stable parameter types), snapshot state, `remember`/`derivedStateOf`, `Modifier` order, unidirectional state and the effect lifecycle (`LaunchedEffect`, `DisposableEffect`, `rememberUpdatedState`) are second nature. Hoist state, keep composables side-effect-free, and design every public composable as a small, deliberate, well-named API. Reach for the simplest idiomatic Compose that meets the requirement, and treat the rules below as house style.

## Two kinds of launcher

**Bottom-sheet launchers** own UI this app renders: a sheet (or an app dialog). They come as a `remember[Name]Launcher` / `[Name]LauncherContent` pair and are what most of this skill is about.

**Activity-result helpers** start someone else's screen and translate the result back: the notification permission request timers need on Android 13+ (`rememberNotificationPermissionRequest`) and system settings screens (`Context.openAppNotificationSettings()`). They deliberately don't follow the launcher shape:

- No `[Name]LauncherContent` and nothing to preview: the system draws every pixel.
- No visibility state and no dismiss: the outcome arrives through an `ActivityResultLauncher` callback.
- Named for what they are (`…PermissionRequest`), never `…Launcher`, so the name doesn't promise the pair.
- They live in `presentation/ui/src/main/java/com/ericwathome/stovetime/ui/helpers/`, one file per contract, and are the only place `rememberLauncherForActivityResult` appears. A screen never calls it inline.
- **Remember them in `<Screen>Screen` (or `<Screen>Route`), never in `<Screen>Content`.** Pass `request::launch` down as a plain callback, and send the result back as an action. Content stays previewable and testable without an Activity.
- The ViewModel decides *whether* to ask (first timer started, rationale, refusals); the screen only launches when the ViewModel says so through a one-off event.
- On device, grant or deny with Maestro's `launchApp: permissions:`; never tap system dialogs.

If you're starting an Activity or asking for a permission, follow those rules and skip the rest. If you're showing a sheet, everything below applies.

## Ownership

App and journey sheets are launchers and live in:

```text
:presentation:ui
presentation/ui/src/main/java/com/ericwathome/stovetime/ui/launchers/<name>/
```

- **Not in feature modules**, even when only one feature uses the sheet today (add a timer is a Timers sheet, and it still lives here). Features trigger journeys; they don't own sheet UI.
- **Not in `:presentation:design`.** The design system owns the generic primitive (`StoveBottomSheet`) and visual building blocks (`StoveCard`, `StoveButton`, `Pill`). The app-aware launcher that arranges them belongs in `:presentation:ui`.
- `StoveBottomSheet` is the only sheet primitive. Never call Material's `ModalBottomSheet` directly outside it. It doesn't exist yet: the first launcher step adds it to `:presentation:design` with `implement-design` (Job B), from the sheet in Figma, and it launches fully expanded (`rememberModalBottomSheetState(skipPartiallyExpanded = true)`). If Figma has no sheet design, stop and ask before building one.

## Naming

```text
[Journey]Launcher
remember[Journey]Launcher
[Journey]LauncherContent
```

Examples for the sheets the app needs:

| Sheet | Launcher |
|---|---|
| Streak details (tap the streak `StatChip`) | `StreakLauncher` |
| Ingredients while cooking | `IngredientsLauncher` |
| Leave cook mode? | `LeaveCookLauncher` |
| Add a timer (Timers tab) | `AddTimerLauncher` |
| Reset progress? (Me tab) | `ResetProgressLauncher` |

Avoid `AddTimerSheet`, `AddTimerBottomSheet`, `ShowAddTimerSheet`, `AddTimerDialog`. `BottomSheet` is only for the generic primitive.

## Launcher shape

```kotlin
class AddTimerLauncher internal constructor(
    private val onLaunch: () -> Unit,
) {
    fun launch() {
        onLaunch()
    }
}
```

The constructor is `internal`; the public API is `remember[Name]Launcher(...)`.

When one sheet serves many requests (one confirmation, many messages), pass the varying input to `launch(...)`, not to `remember`, so a screen holds one launcher instead of one per request.

## remember[Name]Launcher

It owns local visibility, renders the sheet, and returns the launcher. It receives state and callbacks from its host and never injects a ViewModel.

```kotlin
@Composable
fun rememberAddTimerLauncher(
    state: AddTimerSheetState,
    onLabelChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onSave: () -> Unit,
): AddTimerLauncher {
    var isVisible by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    if (isVisible) {
        StoveBottomSheet(onDismiss = { isVisible = false }) {
            AddTimerLauncherContent(
                state = state,
                onLabelChange = onLabelChange,
                onMinutesChange = onMinutesChange,
                onSave = {
                    isVisible = false
                    onSave()
                },
            )
        }
    }

    return remember {
        AddTimerLauncher(
            onLaunch = {
                scope.launch {
                    keyboard?.hide()
                    isVisible = true
                }
            },
        )
    }
}
```

- **Close the keyboard before showing a sheet**, so a sheet is never drawn over it.
- Inside a sheet, the keyboard rules from `implement-design` still apply: form fields and the pinned `StoveButton` stay above the keyboard.
- Back, a scrim tap and a swipe dismiss. If the sheet holds an unsaved form, the host's ViewModel says so (`hasUnsavedChanges` in the state passed in) and the launcher asks before closing.

## Content

`[Name]LauncherContent` is `private` unless another launcher reuses it. It:

- takes plain state and callbacks, plus `modifier: Modifier = Modifier`
- renders UI only, from `:presentation:design` components and `StovetimeTheme` tokens
- never injects a ViewModel, collects a flow, or touches repositories, use cases, navigation or `DataResult`
- gives the sheet title heading semantics, and every icon-only action a content description from `strings.xml`

## Host usage

The host is whatever composable owns the trigger: a screen, a section, a list item, a top bar. Remember the launcher close to the trigger rather than lifting every launcher to the top of the screen.

```kotlin
@Composable
internal fun TimersScreen(viewModel: TimersViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val addTimerLauncher =
        rememberAddTimerLauncher(
            state = state.addTimer,
            onLabelChange = { viewModel.onAction(TimersAction.OnTimerLabelChange(it)) },
            onMinutesChange = { viewModel.onAction(TimersAction.OnTimerMinutesChange(it)) },
            onSave = { viewModel.onAction(TimersAction.OnSaveTimer) },
        )

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            TimersEvent.AskForTimer -> addTimerLauncher.launch()
        }
    }

    TimersContent(state = state, onAction = viewModel::onAction, onClickAddTimer = addTimerLauncher::launch)
}
```

- The host's own interaction or the host's ViewModel event decides when to call `launch()`. A launcher is never launched from an event of a ViewModel that exists only to feed that launcher.
- Pass triggers down as callbacks (`onClickAddTimer = addTimerLauncher::launch`). Never pass a launcher into a design-system component.
- In lazy lists, remember a launcher inside a composable item body, not in a `LazyListScope` extension.

## Callbacks and state

- A completing action **hides the sheet, then calls the host callback**.
- Submissions that load or can fail keep their state in the host's ViewModel (`ViewState<T>`), pass it in, and hide only once success is confirmed.
- The launcher owns visibility and nothing else. Business and data-backed state (timers, progress, XP) lives in the feature ViewModel (or a `:presentation:ui` ViewModel for a journey shared by several features), and reaches the launcher as parameters.
- If a launcher needs something only `:app` can do (start an Activity, read `BuildConfig`), declare a small contract in `:presentation:ui` and let `:app` implement it, injected with a defaulted `koinInject()` parameter. Don't reach into `:app`, and don't give the launcher a ViewModel for it.

## Previews

Preview `[Name]LauncherContent`, not `remember[Name]Launcher`, with `@PreviewLightDark` inside `StovetimePreviewTheme`: sample state, no-op callbacks, and the important states (empty, filled, saving, error, unsaved changes).

## Tests

- **Compose tests** in `presentation/ui/src/androidTest`: `launch()` opens the sheet, dismiss closes it, the completing action hides the sheet and calls the callback once, loading/error states render, and the unsaved-changes prompt appears when expected.
- **ViewModel tests** for everything data-backed the sheet shows.
- **Maestro**: each journey that opens a sheet gets a flow that opens it, fills it in with the keyboard open, saves, and checks the result on the host screen.

## Do

- Name app sheets as launchers and put them in `:presentation:ui/launchers/`.
- Use `remember[Name]Launcher` as the host API and return a `[Name]Launcher` with `launch()`.
- Build on `StoveBottomSheet`; keep content private, dumb and design-system-only.
- Close the keyboard before launching.
- Preview the content directly.

## Don't

- Name an app sheet `*Sheet` or `*BottomSheet`, or put one in a feature module or in `:presentation:design`.
- Call `ModalBottomSheet` outside `StoveBottomSheet`.
- Inject or pass a ViewModel into a launcher or its content.
- Call repositories, use cases, datasources, Ktor, Room or navigation from launcher content.
- Store business or data-backed state in the launcher.
- Call `rememberLauncherForActivityResult` outside a `…Request` helper, or remember a request helper in `Content`.

## Reference

`StoveBottomSheet` in `presentation/design/.../components/` once it exists; `implement-design` (keyboard rules, adding a component); `new-compose-screen` (Route → Screen → Content); `collect-one-time-event`.
