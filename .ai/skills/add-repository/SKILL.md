---
name: add-repository
description: Scaffold a repository in Stovetime's :core:data — a fun-interface per operation plus a parent aggregator, internal Impls that combine the TheMealDB remote and Room local datasources under the injected CachingStrategy, data models, DTO/Cache mappers, and Koin bindings in CoreDataModule.
when_to_use: User asks to "add a repository", expose a datasource to the UI/domain, cache TheMealDB recipes for offline cook mode, read or save progress (XP, streak, completed recipes), or is wiring a new domain end-to-end.
---

# Skill: add-repository

Repositories turn `NetworkResult` / `LocalResult` into `DataResult` and own the caching decision. They live entirely in `:core:data` (`com.ericwathome.stovetime.core.data`). `DataResult` and `DataError` come from `:core:common`; `mapToData` lives in `core:data/helpers`. If `:core:data` doesn't exist yet, create it with `add-feature-module` first.

## Inputs

- `<Domain>`: PascalCase noun (e.g. `Recipe`, `CompletedRecipe`).
- `<Feature>`: the parent group (e.g. `Recipes`, `Progress`).
- Datasources already exist (run `add-datasource` first if not).

## Files

### 1. Data model — `model/<feature>/<Domain>.kt`

```kotlin
@Serializable
data class Recipe(
    val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val imageUrl: String?,
    val ingredients: List<Ingredient>,
    val steps: List<String>,
    val lastUpdated: Long,
)

@Serializable
data class Ingredient(
    val name: String,
    val measure: String,
)
```

Models are UI-ready and framework-free: no `str…` names, no numbered fields, no blank strings standing in for "missing".

### 2. Mappers — `mappers/<feature>/<Domain>ModelMappers.kt`

Three directions:

```kotlin
fun MealDto.toData(): Recipe =
    Recipe(
        id = idMeal,
        name = strMeal,
        category = strCategory?.ifBlank { null },
        area = strArea?.ifBlank { null },
        imageUrl = strMealThumb?.ifBlank { null },
        ingredients = ingredients(),
        steps = strInstructions.orEmpty().lines().map { it.trim() }.filter { it.isNotEmpty() },
        lastUpdated = Now.toEpochMilliseconds(),
    )

fun Recipe.toCache(): RecipeCache =
    RecipeCache(
        id = id,
        name = name,
        category = category,
        area = area,
        imageUrl = imageUrl,
        ingredients = ingredients.map { IngredientCache(it.name, it.measure) },
        steps = steps,
        createdAt = Now.toEpochMilliseconds(),
        updatedAt = Now.toEpochMilliseconds(),
    )

fun RecipeCache.toData(): Recipe =
    Recipe(
        id = id,
        name = name,
        category = category,
        area = area,
        imageUrl = imageUrl,
        ingredients = ingredients.map { Ingredient(it.name, it.measure) },
        steps = steps,
        lastUpdated = updatedAt,
    )
```

`MealDto.ingredients()` pairs `strIngredientN` with `strMeasureN` for N in 1..20 and drops blank pairs; it lives next to these mappers. `Now` is the computed current time from `core/data/.../helpers/DateHelpers.kt`. Use it instead of `Clock.System.now()`, and never inject a `Clock`: tests place cached data relative to the real `Now` (e.g. `Now - 8.days`) instead of freezing time.

### 3. Operation — `repo/<feature>/<Verb><Domain>Repository.kt`

```kotlin
fun interface GetRecipeRepository {
    suspend operator fun invoke(id: String): DataResult<Recipe?>
}

internal class GetRecipeRepositoryImpl(
    private val recipesRemoteDatasource: RecipesRemoteDatasource,
    private val recipeLocalDatasource: RecipeLocalDatasource,
    private val cachingStrategy: CachingStrategy,
) : GetRecipeRepository {
    override suspend fun invoke(id: String): DataResult<Recipe?> {
        val networkResult =
            recipesRemoteDatasource.lookupMeal(id = id).mapToData { response ->
                response.meals?.firstOrNull()?.toData()
            }
        val localResult = recipeLocalDatasource.getRecipe(id = id).mapToData { it?.toData() }

        (networkResult as? DataResult.Success)?.data?.let { recipe ->
            recipeLocalDatasource.saveRecipe(recipe.toCache())
        }

        if (localResult is DataResult.Success && localResult.data == null) {
            return networkResult
        }

        return when (cachingStrategy) {
            is CachingStrategy.LocalFirst -> localResult
            else -> networkResult
        }
    }
}
```

This template calls the network on every read and returns the cache from before the refresh. A cached recipe is what lets cook mode work in a kitchen with no signal, so when a recipe has a freshness rule (serve the cache while it's fresh, refresh when stale, keep the last good copy with an error when a refresh fails), read the cache first, check staleness against `Now`, and save through one transactional `replace` datasource instead.

Local-only repositories (progress: `SaveCompletedRecipeRepository`, `ObserveCompletedRecipesRepository`, which returns a `Flow`) drop the network half, and network-only ones (search) drop the local half. Both still take `CachingStrategy` if behaviour could ever differ.

### 4. Parent aggregator — `repo/<feature>/<Feature>Repository.kt`

Add a `val` to the existing parent or create one:

```kotlin
interface RecipesRepository {
    val getRecipe: GetRecipeRepository
    val searchRecipes: SearchRecipesRepository
}

internal class RecipesRepositoryImpl(
    override val getRecipe: GetRecipeRepository,
    override val searchRecipes: SearchRecipesRepository,
) : RecipesRepository
```

Callers use it as `repository.getRecipe(id)`.

### 5. Koin — `di/CoreDataModule.kt`

```kotlin
internal val RecipesRepositoryModule =
    module {
        // parent
        singleOf(::RecipesRepositoryImpl) {
            bind<RecipesRepository>()
        }
        // children
        singleOf(::GetRecipeRepositoryImpl) {
            bind<GetRecipeRepository>()
        }
    }
```

Add a new module to `RepositoryModule.includes(...)`. `CachingStrategy` is provided once there as a single (`LocalFirst`); the first repository creates that binding, later ones reuse it. `CoreDataModule` also includes the datasource modules, so `AppModule` only includes `CoreDataModule`.

## Rules

- Public `fun interface` + `internal` Impl; only interfaces leave the module.
- Convert with `mapToData { }`; never hand-roll `when` over `NetworkResult`/`LocalResult`.
- Return `DataResult<T>` (or `Flow<T>` for streams like live XP); never throw.
- Branch on the injected `CachingStrategy`, not on ad-hoc "is online" checks.
- XP and streak rules are business logic, not data work: they go in a use case (`add-usecase`) once two screens need them, never in a repository.
- Tests in `core/data/src/test/java/com/ericwathome/stovetime/core/data/repo/`: mock the parent datasources with MockK, construct the Impl directly with an explicit `CachingStrategy`, and cover success, error, `{"meals": null}` and the empty-cache first-launch branch. Keep TheMealDB JSON fixtures in `core/data/src/test/.../helpers/`.
- Finish with the `checks` skill.

## Reference

None yet. When the first repository lands, list its operation, parent, Koin module and test here.
