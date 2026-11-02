package com.ericwathome.stovetime.core.data.repo.recipes

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.helpers.Now
import com.ericwathome.stovetime.core.data.helpers.mapToData
import com.ericwathome.stovetime.core.data.mappers.recipes.toCatalogueMealCache
import com.ericwathome.stovetime.datasources.local.models.CatalogueMealCache
import com.ericwathome.stovetime.datasources.local.models.CategoryCache
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.recipes.catalogue.CatalogueLocalDatasource
import com.ericwathome.stovetime.datasources.local.sources.settings.RecipeSyncLocalDatasource
import com.ericwathome.stovetime.datasources.network.sources.recipes.RecipesRemoteDatasource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun interface SyncCatalogueRepository {
    suspend operator fun invoke(): DataResult<Unit>
}

internal class SyncCatalogueRepositoryImpl(
    private val recipesRemoteDatasource: RecipesRemoteDatasource,
    private val catalogueLocalDatasource: CatalogueLocalDatasource,
    private val cookLocalDatasource: CookLocalDatasource,
    private val recipeSyncLocalDatasource: RecipeSyncLocalDatasource,
) : SyncCatalogueRepository {
    private val mutex = Mutex()

    override suspend fun invoke(): DataResult<Unit> {
        if (!mutex.tryLock()) {
            mutex.withLock { }
            return DataResult.Success(Unit)
        }
        return try {
            val syncedAt = Now.toEpochMilliseconds()
            sync(syncedAt).also { result ->
                when (result) {
                    is DataResult.Success -> recipeSyncLocalDatasource.lastSyncedAt.save(syncedAt)
                    is DataResult.Error -> recipeSyncLocalDatasource.lastFailedAt.save(syncedAt)
                }
            }
        } finally {
            mutex.unlock()
        }
    }

    private suspend fun sync(syncedAt: Long): DataResult<Unit> {
        val categories =
            when (
                val result =
                    recipesRemoteDatasource.getCategories().mapToData { response ->
                        response.categories.orEmpty().map { it.strCategory.trim() }.filter { it.isNotEmpty() }.distinct()
                    }
            ) {
                is DataResult.Success -> result.data
                is DataResult.Error -> return result
            }

        val freshMeals =
            when (val result = fetchMeals(categories, syncedAt)) {
                is DataResult.Success -> result.data
                is DataResult.Error -> return result
            }
        val storedMeals =
            when (val result = catalogueLocalDatasource.getMeals().mapToData { it }) {
                is DataResult.Success -> result.data
                is DataResult.Error -> return result
            }
        val cookedIds =
            when (val result = cookLocalDatasource.getCookedRecipeIds().mapToData { it }) {
                is DataResult.Success -> result.data
                is DataResult.Error -> return result
            }

        val storedById = storedMeals.associateBy { it.id }
        val freshIds = freshMeals.map { it.id }.toSet()
        val upserts = freshMeals.filter { meal -> storedById[meal.id]?.let { it.differsFrom(meal) } ?: true }
        val deleteIds = storedMeals.map { it.id }.filter { it !in freshIds && it !in cookedIds }

        return catalogueLocalDatasource
            .replaceCatalogue(
                categories = categories.mapIndexed { index, name -> CategoryCache(name = name, position = index, syncedAt = syncedAt) },
                upserts = upserts,
                deleteIds = deleteIds,
            ).mapToData { }
    }

    private suspend fun fetchMeals(
        categories: List<String>,
        syncedAt: Long,
    ): DataResult<List<CatalogueMealCache>> {
        val results =
            coroutineScope {
                categories
                    .map { category ->
                        async {
                            recipesRemoteDatasource.getMealsByCategory(category).mapToData { response ->
                                response.meals.orEmpty().map { it.toCatalogueMealCache(category = category, syncedAt = syncedAt) }
                            }
                        }
                    }.awaitAll()
            }
        results.filterIsInstance<DataResult.Error>().firstOrNull()?.let { return it }
        return DataResult.Success(
            results
                .filterIsInstance<DataResult.Success<List<CatalogueMealCache>>>()
                .flatMap { it.data }
                .distinctBy { it.id },
        )
    }

    private fun CatalogueMealCache.differsFrom(other: CatalogueMealCache): Boolean =
        name != other.name || photo != other.photo || category != other.category
}
