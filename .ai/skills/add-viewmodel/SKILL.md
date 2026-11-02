---
name: add-viewmodel
description: Add or change a Stovetime ViewModel on the MviViewModel<Action, State, Event> base — sealed Action/Event, immutable State with ViewState<T> fields, onStateSubscription for loading, updateState/sendEvent, DataResult onSuccess/onError bridging, and a viewModelOf Koin binding.
when_to_use: User asks to add/modify a ViewModel, add an action or state field, load recipes or progress into a screen, or emit a one-off UI event. Companion to new-compose-screen.
---

# Skill: add-viewmodel

Every screen ViewModel extends `MviViewModel` from `:presentation:ui` (`presentation/ui/src/main/java/com/ericwathome/stovetime/ui/helpers/MviViewModel.kt`). If `:presentation:ui` or the base class doesn't exist yet, create it in the step that adds the first screen ViewModel; the shape is below. Action, State, Event and the ViewModel share one file: `features/<feature>/src/main/java/com/ericwathome/stovetime/features/<feature>/<screen>/<Screen>ViewModel.kt`.

## The base class

```kotlin
abstract class MviViewModel<Action, State, Event>(
    initialState: State,
) : ViewModel() {
    private val _event = Channel<Event>()
    val event = _event.receiveAsFlow()

    private val _state = MutableStateFlow(initialState)
    val state =
        _state
            .onStart { onStateSubscription() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    protected val currentState: State
        get() = _state.value

    protected open fun onStateSubscription() {}

    open fun onAction(action: Action) {}

    protected fun updateState(block: (State) -> State) {
        _state.update(block)
    }

    protected fun sendEvent(block: () -> Event) {
        viewModelScope.launch { _event.send(block()) }
    }
}
```

## Contract

```kotlin
sealed interface RecipeIntroAction {
    data object OnClickRetry : RecipeIntroAction

    data object OnClickStartCooking : RecipeIntroAction
}

sealed interface RecipeIntroEvent {
    data class OpenCookMode(
        val recipeId: String,
    ) : RecipeIntroEvent
}

data class RecipeIntroState(
    val recipe: ViewState<Recipe> = ViewState.Initial,
) {
    val isStartEnabled: Boolean
        get() = recipe is ViewState.Success
}

class RecipeIntroViewModel(
    private val recipeId: String,
    private val recipesRepository: RecipesRepository,
) : MviViewModel<RecipeIntroAction, RecipeIntroState, RecipeIntroEvent>(RecipeIntroState()) {
    override fun onStateSubscription() {
        loadRecipe()
    }

    override fun onAction(action: RecipeIntroAction) {
        when (action) {
            RecipeIntroAction.OnClickRetry -> {
                loadRecipe()
            }

            RecipeIntroAction.OnClickStartCooking -> {
                onClickStartCooking()
            }
        }
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            updateState { it.copy(recipe = ViewState.Loading) }
            recipesRepository
                .getRecipe(recipeId)
                .onSuccess { recipe ->
                    updateState { it.copy(recipe = ViewState.Success(recipe)) }
                }.onError { error ->
                    updateState { it.copy(recipe = ViewState.Error(error)) }
                }
        }
    }

    private fun onClickStartCooking() {
        if (!currentState.isStartEnabled) return
        sendEvent { RecipeIntroEvent.OpenCookMode(recipeId) }
    }
}
```

## Rules

- **Loading starts in `onStateSubscription()`**, not `init`. The base class calls it when the UI first collects `state`, so tests and previews don't hit TheMealDB or Room on construction.
- Read the latest state inside the ViewModel with `currentState`, never `state.value`: the public `state` is a `stateIn` copy that can lag behind an `updateState` that just ran.
- Mutate state only via `updateState { it.copy(...) }`; one-off effects (navigation, launching a sheet, asking for the notification permission) via `sendEvent { ... }`, collected in the screen with `CollectOneTimeEvent` (`collect-one-time-event`). Don't put one-off effects in `State`.
- Every async field is a `ViewState<T>` (`Initial` → `Loading` → `Success`/`Error`). Bridge `DataResult` with `onSuccess` / `onError` from `:core:data` (`helpers/DataResult.kt`). Never `when` on `DataResult` in the ViewModel, and never catch exceptions there: data calls return results.
- Derived UI flags (`isStartEnabled`, `isLastStep`, `xpEarned`) are computed properties on `State`, not stored fields.
- `onAction` is a flat `when` that delegates to `private fun on<Action>(...)` handlers; every branch uses braces.
- Inject repository/use-case **aggregators** (`RecipesRepository`, `ProgressRepository`, `PathUseCases`), not individual `fun interface`s. No `Context`, no Room DAO, no timer engine in a ViewModel.
- Navigation arguments (`recipeId`, step index) are constructor parameters, passed with `parametersOf` from the screen (`new-compose-screen`).
- Combining reactive inputs (saved progress from Room → the learning path, or running timers → the cook step) uses `flatMapLatest`/`combine` + `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), …)`.
- Until a repository exists for the data, keep sample data in a `private val` in the ViewModel file, not in the screen.

## Koin

`features/<feature>/src/main/java/com/ericwathome/stovetime/features/<feature>/di/<Feature>Module.kt`:

```kotlin
internal val RecipesViewModelModule =
    module {
        viewModelOf(::RecipeIntroViewModel)
    }

val RecipesModule =
    module {
        includes(RecipesViewModelModule)
    }
```

The feature's public `<Feature>Module` includes its ViewModel module, and `:app`'s `di/AppModule.kt` includes the feature module. Koin only: no Hilt, no manual factories.

## Tests

`features/<feature>/src/test/java/.../<screen>/<Screen>ViewModelTest.kt` — see `generate-unit-test`. Use the `MainDispatcherRule` with `UnconfinedTestDispatcher()`, `@MockK` relaxed dependencies, and Turbine on `viewModel.state`, asserting the `Initial → Loading → Success/Error` sequence. Test each event with Turbine on `viewModel.event`.

## Reference

`presentation/ui/.../helpers/MviViewModel.kt`, `presentation/ui/.../helpers/StateHelpers.kt` (`ViewState`), `core/data/.../helpers/DataResult.kt`. Each is created by the first step that needs it; once a feature exists, its ViewModel and `di/<Feature>Module.kt` are the reference.
