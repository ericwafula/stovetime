---
name: add-viewmodel
description: Add or change a Stovetime ViewModel — sealed Action, immutable State data class, StateFlow exposed read-only, a single onAction entry point, one-off events through a Channel, and a viewModelOf Koin binding.
when_to_use: User asks to add/modify a ViewModel, add an action or state field, load data into a screen, or emit a one-off UI event. Companion to new-compose-screen.
---

# Skill: add-viewmodel

Action, State, Event and the ViewModel share one file: `<feature>/<screen>/<Screen>ViewModel.kt`.

## Contract

```kotlin
sealed interface <Screen>Action {
    data object OnClickNewTicket : <Screen>Action

    data class OnSelectCategory(
        val categoryId: String,
    ) : <Screen>Action
}

sealed interface <Screen>Event {
    data class OpenGuide(
        val guideId: String,
    ) : <Screen>Event
}

data class <Screen>State(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
) {
    val isEmpty: Boolean
        get() = !isLoading && categories.isEmpty()
}

class <Screen>ViewModel : ViewModel() {
    private val _state = MutableStateFlow(<Screen>State())
    val state: StateFlow<<Screen>State> = _state.asStateFlow()

    private val _event = Channel<<Screen>Event>()
    val event: Flow<<Screen>Event> = _event.receiveAsFlow()

    fun onAction(action: <Screen>Action) {
        when (action) {
            <Screen>Action.OnClickNewTicket -> {
                onClickNewTicket()
            }

            is <Screen>Action.OnSelectCategory -> {
                onSelectCategory(action.categoryId)
            }
        }
    }

    private fun onSelectCategory(categoryId: String) {
        viewModelScope.launch { _event.send(<Screen>Event.OpenGuide(categoryId)) }
    }
}
```

## Rules

- State is one immutable data class. Change it only with `_state.update { it.copy(...) }`.
- Derived flags are computed properties on `State`, not stored fields.
- One-off effects (navigation, snackbars) go through `event`, never into `State`.
- `onAction` is a flat `when` that delegates to `private fun on<Action>(...)` handlers. Every branch uses braces.
- Inject repositories through the constructor. No `Context`, no Room, no timer engine.
- Until a repository exists for the data, keep sample data in a `private val` in the ViewModel file, not in the screen.

## Koin

`<feature>/di/<Feature>Module.kt`:

```kotlin
val <Feature>Module =
    module {
        viewModelOf(::<Screen>ViewModel)
    }
```

Include it in `di/AppModule.kt`.

## Tests

Write `<Screen>ViewModelTest` with `generate-unit-test`: initial state, each action, and each event.
