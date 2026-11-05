---
name: share-image
description: Share a picture of something in Stovetime (today, the cook card from the finish screen) through the phone's share sheet, using the ready-made helpers in :presentation:ui — rememberImageShare, ShareImageSource and ShareCookCard — wired the house way, with a ViewModel action and a one-off event.
when_to_use: "Share", "share your cook", "share sheet", "share a picture/image/card", the TODO(demo) marker in FinishScreen, spec 5.7, or any screen that needs to send an image to another app.
---

# Skill: share-image

## What's already built

Everything that touches files, bitmaps and other apps lives in `:presentation:ui`. Don't rebuild any of it.

| Piece | Where | What it does |
|---|---|---|
| `ShareCookCard(name, photo, xpEarned, cookMillis, weekStreak)` | `ui/components/ShareCookCard.kt` | The 360dp card that gets shared: dish photo in the green ring, the name, "Cooked on Stovetime", and the XP, cook time and week streak tiles |
| `rememberImageShare()` | `ui/helpers/ShareHelpers.kt` | Returns an `ImageShare` for this screen |
| `ShareImageSource(share) { … }` | `ui/helpers/ShareHelpers.kt` | Draws its content off screen, invisible and hidden from TalkBack, so it can be captured. Takes no space in the layout |
| `share.send(caption)` | `ui/helpers/ShareHelpers.kt` | Suspends: captures the content as a PNG, saves it to `cache/shared/cook.png` and opens the share sheet with the image and the caption |

The FileProvider (`${applicationId}.share`) is in the `:presentation:ui` manifest, so no manifest changes are needed.

## Wiring it into a screen

Follow the same path as every other one-off effect (`add-viewmodel`, `collect-one-time-event`):

1. **Action.** Add `data object OnClickShare` to the screen's `Action`.
2. **Event.** Add an event that carries the data the caption needs, for example `data class Share(val name: String, val cookMillis: Long, val weekStreak: Int)`. The ViewModel sends it from `onAction`, only when the dish has loaded.
3. **Screen.** In `<Screen>Screen` (it owns the ViewModel), remember the share and a coroutine scope, place the source, and send from the event:

   ```kotlin
   val share = rememberImageShare()
   val scope = rememberCoroutineScope()
   val shareCaption = stringResource(R.string.finish_share_caption)

   CollectOneTimeEvent(viewModel.event) { event ->
       when (event) {
           FinishEvent.Continue -> onContinue()
           is FinishEvent.Share -> scope.launch { share.send(shareCaption.format(event.name)) }
       }
   }

   (state.dish as? ViewState.Success)?.data?.let { dish ->
       ShareImageSource(share) {
           ShareCookCard(dish.name, dish.photo, dish.xpEarned, dish.cookMillis, state.progress?.weekStreak ?: 0)
       }
   }
   ```

4. **Button.** In `<Screen>Content`, a `StoveButton` with `StoveButtonStyle.Secondary` and a test tag (`finish_share`) that calls `onAction(OnClickShare)`. On the finish screen it goes where the `TODO(demo)` marker is, above Continue. Delete the marker.
5. **Words.** The caption and the button label go in the feature's `strings.xml` (`checkHardcodedText` fails the build otherwise). Use `cookTimeText` for the time so it matches the card.

## Rules

- `ShareImageSource` only captures what's composed, so place it only once the data is there, and keep it in the composition while the screen is open.
- Never pass a `Context`, `Bitmap` or `Uri` to the ViewModel. It decides *what* to share; the screen does the sharing.
- `send` is a suspend function: call it from `rememberCoroutineScope`, not from `LaunchedEffect` keyed on state.
- One tap, one share. Don't add a loading state: capturing the card takes a few milliseconds.

## Tests

- **ViewModel (Turbine):** `OnClickShare` sends `Share` with this cook's name, cook time and streak; it sends nothing while the dish is still loading.
- **Caption:** if the wording depends on the numbers (no streak yet, a cook over an hour), put that choice in a small `internal` function the tests can call, and keep the strings themselves in `strings.xml`.
- The helpers have their own tests in `presentation/ui/src/test/.../ShareHelpersTest.kt`. Check the share sheet itself on a phone: tap Share once, and you should see "1 image" with the card and the caption.
