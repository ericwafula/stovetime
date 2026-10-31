---
name: new-compose-screen
description: Build a Stovetime Compose screen as Route → Screen → Content — public <Screen>Route, internal <Screen>Screen that gets the ViewModel with koinViewModel and collects state with lifecycle, private stateless <Screen>Content(state, onAction), previews for every state, theme tokens instead of hardcoded colours, and strings in strings.xml.
when_to_use: User asks for a new screen/page/view, shares a screenshot or Figma frame to build, or asks to split a large composable into Route → Screen → Content.
---

# Skill: new-compose-screen

## Inputs

- `<Screen>`: PascalCase name, e.g. `Kitchen`.
- `<feature>`: the product area, e.g. `kitchen`, `timers`, `guides`.
- The design: a Figma frame (read it with the Figma MCP) or a screenshot.

## Files

Package `com.ericwathome.stovetime.<feature>.<screen>`:

1. **`<Screen>ViewModel.kt`**: follow `add-viewmodel`.
2. **`<Screen>Screen.kt`**:

```kotlin
@Composable
fun <Screen>Route(modifier: Modifier = Modifier) {
    <Screen>Screen(modifier = modifier)
}

@Composable
internal fun <Screen>Screen(
    modifier: Modifier = Modifier,
    viewModel: <Screen>ViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    <Screen>Content(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun <Screen>Content(
    state: <Screen>State,
    onAction: (<Screen>Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    // render state; every tap calls onAction(<Screen>Action.…)
}

@Preview(showBackground = true)
@Composable
private fun <Screen>ContentPreview() {
    MaterialTheme {
        <Screen>Content(
            state = <Screen>State(/* sample data */),
            onAction = {},
        )
    }
}
```

3. **Koin**: register the ViewModel in `<feature>/di/<Feature>Module.kt` and include that module in `di/AppModule.kt`.
4. **Wiring**: until navigation exists (roadmap 0.1), render `<Screen>Route()` from `MainActivity`.

## Rules

- Only `Route` is public. `Screen` is `internal`; `Content` and previews are `private`.
- `Content` is stateless: `state` + `onAction` + `modifier`, nothing else, so it previews without Koin.
- Split big screens into small private composables per section (header, category row, rail, guide card), each with its own preview.
- Colours, type and shapes come from `MaterialTheme`. Never write `Color(0xFF…)` or a font size in a screen. If the design needs a value the theme doesn't have, add it to the theme.
- Every user-facing string goes in `res/values/strings.xml`. Sample data in previews can be literal.
- Images: use `painterResource` with drawables in `res/drawable`, named after what they show (`guide_boiled_eggs`).
- Touch targets are at least 48dp.
- Use `collectAsStateWithLifecycle()`, not `collectAsState()`.
- `MainActivity` calls `enableEdgeToEdge()`, so pad for system bars.
- Finish with the `checks` skill.

## Reference

`MainActivity.kt` (`AppName` and its preview) for theme and string usage.
