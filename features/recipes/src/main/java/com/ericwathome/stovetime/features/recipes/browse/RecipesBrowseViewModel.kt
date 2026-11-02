package com.ericwathome.stovetime.features.recipes.browse

import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.common.helpers.Elapsed
import com.ericwathome.stovetime.core.common.helpers.elapsedBetween
import com.ericwathome.stovetime.core.common.result.onError
import com.ericwathome.stovetime.core.common.result.onSuccess
import com.ericwathome.stovetime.core.data.helpers.Now
import com.ericwathome.stovetime.core.data.helpers.ObserveConnectivity
import com.ericwathome.stovetime.core.data.model.recipes.Catalogue
import com.ericwathome.stovetime.core.data.model.recipes.CatalogueMeal
import com.ericwathome.stovetime.core.data.model.recipes.RecipeSyncStatus
import com.ericwathome.stovetime.core.data.repo.recipes.RecipesRepository
import com.ericwathome.stovetime.core.data.workers.recipes.StartRecipeSyncWork
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.dataOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

sealed interface RecipesBrowseAction {
    data class OnChangeQuery(
        val query: String,
    ) : RecipesBrowseAction

    data class OnSelectCategory(
        val category: String?,
    ) : RecipesBrowseAction

    data object OnRefresh : RecipesBrowseAction

    data object OnClickRetry : RecipesBrowseAction

    data class OnClickRecipe(
        val recipeId: String,
    ) : RecipesBrowseAction
}

sealed interface RecipesBrowseEvent {
    data class OpenRecipe(
        val recipeId: String,
    ) : RecipesBrowseEvent
}

sealed interface BrowseContent {
    data object Loading : BrowseContent

    data class Empty(
        val isOffline: Boolean,
    ) : BrowseContent

    data class NoMatches(
        val query: String,
    ) : BrowseContent

    data class Meals(
        val meals: List<CatalogueMeal>,
    ) : BrowseContent
}

data class RecipesBrowseState(
    val query: String = "",
    val selectedCategory: String? = null,
    val catalogue: ViewState<Catalogue> = ViewState.Initial,
    val syncStatus: RecipeSyncStatus = RecipeSyncStatus(),
    val isOnline: Boolean = true,
    val lastSynced: Elapsed? = null,
) {
    val categories: List<String>
        get() = catalogue.dataOrNull()?.categories.orEmpty()

    val isRefreshing: Boolean
        get() = syncStatus.isSyncing && isOnline

    private val isFirstSyncPending: Boolean
        get() = isOnline && syncStatus.lastSyncedAt == null && syncStatus.lastFailedAt == null

    val content: BrowseContent
        get() =
            when (catalogue) {
                ViewState.Initial, ViewState.Loading -> {
                    BrowseContent.Loading
                }

                is ViewState.Error -> {
                    BrowseContent.Empty(isOffline = !isOnline)
                }

                is ViewState.Success -> {
                    when {
                        catalogue.data.storedCount == 0 && (isRefreshing || isFirstSyncPending) -> BrowseContent.Loading
                        catalogue.data.storedCount == 0 -> BrowseContent.Empty(isOffline = !isOnline)
                        catalogue.data.meals.isEmpty() -> BrowseContent.NoMatches(query = query.trim())
                        else -> BrowseContent.Meals(catalogue.data.meals)
                    }
                }
            }
}

private data class CatalogueFilter(
    val category: String? = null,
    val query: String = "",
)

@OptIn(ExperimentalCoroutinesApi::class)
class RecipesBrowseViewModel(
    private val recipesRepository: RecipesRepository,
    private val startRecipeSyncWork: StartRecipeSyncWork,
    private val observeConnectivity: ObserveConnectivity,
) : MviViewModel<RecipesBrowseAction, RecipesBrowseState, RecipesBrowseEvent>(RecipesBrowseState()) {
    private val filter = MutableStateFlow(CatalogueFilter())
    private var isObserving = false

    override fun onStateSubscription() {
        if (isObserving) return
        isObserving = true
        observeCatalogue()
        observeSyncStatus()
        observeConnectivity()
    }

    override fun onAction(action: RecipesBrowseAction) {
        when (action) {
            is RecipesBrowseAction.OnChangeQuery -> {
                onChangeQuery(action.query)
            }

            is RecipesBrowseAction.OnSelectCategory -> {
                onSelectCategory(action.category)
            }

            RecipesBrowseAction.OnRefresh -> {
                startRecipeSyncWork()
            }

            RecipesBrowseAction.OnClickRetry -> {
                startRecipeSyncWork()
            }

            is RecipesBrowseAction.OnClickRecipe -> {
                sendEvent { RecipesBrowseEvent.OpenRecipe(action.recipeId) }
            }
        }
    }

    private fun onChangeQuery(query: String) {
        updateState { it.copy(query = query) }
        filter.update { it.copy(query = query) }
    }

    private fun onSelectCategory(category: String?) {
        updateState { it.copy(selectedCategory = category) }
        filter.update { it.copy(category = category) }
    }

    private fun observeCatalogue() {
        filter
            .flatMapLatest { recipesRepository.observeCatalogue(category = it.category, query = it.query) }
            .onEach { result ->
                result
                    .onSuccess { catalogue -> updateState { it.copy(catalogue = ViewState.Success(catalogue)) } }
                    .onError { error -> updateState { it.copy(catalogue = ViewState.Error(error)) } }
            }.launchIn(viewModelScope)
    }

    private fun observeSyncStatus() {
        recipesRepository
            .observeSyncStatus()
            .onEach { status ->
                updateState {
                    it.copy(
                        syncStatus = status,
                        lastSynced = status.lastSyncedAt?.let { syncedAt -> elapsedBetween(syncedAt, Now.toEpochMilliseconds()) },
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun observeConnectivity() {
        observeConnectivity
            .invoke()
            .onEach { isOnline -> updateState { it.copy(isOnline = isOnline) } }
            .launchIn(viewModelScope)
    }
}
