---
name: collect-one-time-event
description: Collect a Stovetime ViewModel's one-off events (navigation, launching a sheet or the notification permission request, opening system settings) in Compose with the lifecycle-aware CollectOneTimeEvent helper from :presentation:ui, instead of a hand-written LaunchedEffect.
when_to_use: A composable reacts to viewModel.event (or any one-off Flow); a new screen or launcher host needs navigation or other effects from its ViewModel; reviewing code that calls .event.collect inside LaunchedEffect.
---

# Skill: collect-one-time-event

## The helper

`presentation/ui/src/main/java/com/ericwathome/stovetime/ui/helpers/EventHelpers.kt` (create it with the first screen that sends events):

```kotlin
@Composable
fun <T> CollectOneTimeEvent(
    events: Flow<T>,
    vararg keys: Any?,
    onEvent: suspend (T) -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(events, lifecycle, *keys) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                events.collect { currentOnEvent(it) }
            }
        }
    }
}
```

It takes the lifecycle from `LocalLifecycleOwner.current`, collects only while the screen is at least `STARTED` (`repeatOnLifecycle`), and collects on `Dispatchers.Main.immediate`, so an event is handled in the same frame it's sent and isn't queued behind other main-thread work. `onEvent` is kept up to date with `rememberUpdatedState`, so the latest lambda always runs without restarting the collection.

`MviViewModel.event` is a rendezvous `Channel`: an event sent while the screen is stopped waits in `sendEvent` and arrives once the screen starts again. Nothing is lost, and nothing fires while the app is in the background, which matters in cook mode, where a step timer can finish while the phone is locked.

## Usage

Collect in `<Screen>Screen` (or `<Screen>Route`), the composable that owns the ViewModel, never in `<Screen>Content`:

```kotlin
@Composable
internal fun CookStepScreen(
    onClose: () -> Unit,
    onFinish: (recipeId: String) -> Unit,
    viewModel: CookStepViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val leaveCookLauncher = rememberLeaveCookLauncher(onLeave = { viewModel.onAction(CookStepAction.OnConfirmLeave) })

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            CookStepEvent.AskToLeave -> leaveCookLauncher.launch()
            CookStepEvent.Close -> onClose()
            is CookStepEvent.Finish -> onFinish(event.recipeId)
        }
    }

    CookStepContent(state = state, onAction = viewModel::onAction)
}
```

- Use an exhaustive `when` on the sealed `<Screen>Event`, so a new event type fails to compile until it's handled.
- Launchers and permission requests are remembered first and launched from the event (`CookStepEvent.AskToLeave -> leaveCookLauncher.launch()`), as in `add-launcher`.
- Pass `keys` only when the collection itself must restart because a value it depends on changed (rare: the handler already sees the latest values).

## Don't

- Write `LaunchedEffect(Unit) { viewModel.event.collect { … } }`: it keeps collecting while the app is in the background and can fire navigation on a stopped screen.
- Collect events in `Content`, a section or a `:presentation:design` component.
- Put one-off effects in `State` and clear them afterwards; that's what events are for (`add-viewmodel`).

## Tests

`presentation/ui/src/androidTest/.../helpers/CollectOneTimeEventTest.kt` checks both behaviours with a `TestLifecycleOwner` provided through `LocalLifecycleOwner`: events arrive while started, and events sent while stopped wait until the screen starts again. Screen ViewModels are tested at the ViewModel level with Turbine on `viewModel.event`.
