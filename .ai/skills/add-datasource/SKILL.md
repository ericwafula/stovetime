---
name: add-datasource
description: Scaffold a network (Ktor, TheMealDB) or local (Room or DataStore) datasource in Stovetime — one fun-interface per operation, an internal Impl wrapped in safeApiCall/safeDatabaseCall, a parent aggregator interface, DTO/Entity/Cache models, mappers and Koin bindings.
when_to_use: User asks to "add a datasource", "call a new TheMealDB endpoint", "store X on the phone", "add a DAO/table", "save a setting", or is wiring a new domain end-to-end (datasources → core:data → core:domain → feature).
---

# Skill: add-datasource

Datasources are the only layer that touches Ktor, Room or DataStore. Everything above them sees `NetworkResult<T>` / `LocalResult<T>` and plain models, never an exception.

## Inputs

- `<Domain>`: PascalCase noun (e.g. `Meal`, `CompletedRecipe`).
- `<Verb>`: `Get`, `Search`, `Create`, `Delete`, `Update` … one per operation.
- Kind: **network** (`:datasources:network`), **local** (`:datasources:local`) or both.

Package root: `com.ericwathome.stovetime.datasources.<network|local>`, sources live under `sources/<feature>/` (local adds a `<camelDomain>/` sub-package, e.g. `sources/progress/completedRecipe/`).

If the module doesn't exist yet, create it with `add-feature-module` (`stovetime.android.library`). The first network datasource also adds the `HttpClient`, `Route`, `NetworkResult` and `safeApiCall`; the first local one adds `StovetimeDatabase`, `LocalResult` and `safeDatabaseCall`.

## Network datasource (`:datasources:network`)

TheMealDB is the only remote source: read-only JSON at `https://www.themealdb.com/api/json/v1/1/`, no auth. The `1` in the path is the free test key. Never add writes, auth or a second host.

### 1. DTO — `dtos/<Domain>Dto.kt`

TheMealDB wraps every list in `{"meals": [...]}` and returns `{"meals": null}` when nothing matches, so the wrapper's list is nullable:

```kotlin
@Serializable
data class MealsResponseDto(
    val meals: List<MealDto>? = null,
)

@Serializable
data class MealDto(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String? = null,
    val strArea: String? = null,
    val strInstructions: String? = null,
    val strMealThumb: String? = null,
    val strIngredient1: String? = null,
    val strMeasure1: String? = null,
)
```

DTOs are public (core:data maps them) and mirror the JSON exactly, including TheMealDB's `str…` names and the numbered `strIngredient1…20` / `strMeasure1…20` fields; turning those into lists is the repository mapper's job. The shared `Json` sets `ignoreUnknownKeys = true`. Empty strings and `null` both mean "missing".

### 2. Route — add a case to `helpers/Route.kt`

```kotlin
sealed class Route(
    path: String,
) {
    val url: String = BuildConfig.MEALDB_BASE_URL + path

    data object SearchMeals : Route("search.php")

    data object LookupMeal : Route("lookup.php")
}
```

The base URL (with the key) is one `buildConfigField` in `datasources/network/build.gradle.kts`; never repeat it in code. Query values go through Ktor's `parameter(...)` so they're encoded, not string interpolation.

### 3. Operation — `sources/<feature>/<Verb><Domain>RemoteDatasource.kt`

```kotlin
fun interface SearchMealsRemoteDatasource {
    suspend operator fun invoke(query: String): NetworkResult<MealsResponseDto>
}

internal class SearchMealsRemoteDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val client: HttpClient,
) : SearchMealsRemoteDatasource {
    override suspend fun invoke(query: String): NetworkResult<MealsResponseDto> =
        safeApiCall(coroutineContext = dispatchers.io) {
            client.get(urlString = Route.SearchMeals.url) {
                parameter("s", query)
            }
        }
}
```

### 4. Parent aggregator — `sources/<feature>/<Feature>RemoteDatasource.kt`

Add a `val` to the existing parent (e.g. `RecipesRemoteDatasource`) or create one:

```kotlin
interface RecipesRemoteDatasource {
    val searchMeals: SearchMealsRemoteDatasource
    val lookupMeal: LookupMealRemoteDatasource
}

internal class RecipesRemoteDatasourceImpl(
    override val searchMeals: SearchMealsRemoteDatasource,
    override val lookupMeal: LookupMealRemoteDatasource,
) : RecipesRemoteDatasource
```

### 5. Koin — `di/NetworkModule.kt`, inside `NetworkSourcesModule`

```kotlin
// parent
singleOf(::RecipesRemoteDatasourceImpl) {
    bind<RecipesRemoteDatasource>()
}
// children
singleOf(::SearchMealsRemoteDatasourceImpl) {
    bind<SearchMealsRemoteDatasource>()
}
```

## Local datasource (`:datasources:local`)

Room holds everything the player earns (completed recipes, XP, streak weeks) and any TheMealDB recipes cached for offline cook mode. Progress exists only on the phone, so a destructive migration loses it: never use one without the user's agreement.

### 1. Entity + Cache — `models/<Domain>Entity.kt`

```kotlin
@Entity
internal data class CompletedRecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: String,
    val xpEarned: Int,
    val completedAt: Long,
)

@Serializable
data class CompletedRecipeCache(
    val id: Long,
    val recipeId: String,
    val xpEarned: Int,
    val completedAt: Long,
)
```

The `Entity` is `internal` (Room only); the `Cache` is the public shape core:data sees. Use `@Embedded` for nested value objects. Moments are epoch milliseconds (`Long`), calendar days ISO strings (`date-helpers`).

### 2. Mappers — `mappers/<Domain>EntityMappers.kt`

```kotlin
internal fun CompletedRecipeEntity.toCache(): CompletedRecipeCache =
    CompletedRecipeCache(id = id, recipeId = recipeId, xpEarned = xpEarned, completedAt = completedAt)

internal fun CompletedRecipeCache.toEntity(): CompletedRecipeEntity =
    CompletedRecipeEntity(id = id, recipeId = recipeId, xpEarned = xpEarned, completedAt = completedAt)
```

### 3. DAO — `daos/<Domain>Dao.kt`

```kotlin
@Dao
internal interface CompletedRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedRecipe(item: CompletedRecipeEntity)

    @Query("SELECT * FROM completedrecipeentity WHERE completedAt >= :since ORDER BY completedAt DESC")
    suspend fun getCompletedSince(since: Long): List<CompletedRecipeEntity>

    @Query("SELECT * FROM completedrecipeentity ORDER BY completedAt DESC")
    fun observeCompletedRecipes(): Flow<List<CompletedRecipeEntity>>
}
```

Room's default table name is the entity class name lower-cased (`completedrecipeentity`). Totals the UI shows live (XP, streak) come from a `Flow` query, not a one-shot read.

### 4. Database — `database/StovetimeDatabase.kt`

- Add the entity to `entities = [...]` and an `abstract val <camelDomain>Dao: <Domain>Dao`.
- **Bump `version`** and add a migration (or confirm with the user that a destructive migration is acceptable). Schemas are exported to `datasources/local/schemas/`; commit the new JSON.

### 5. Operations — `sources/<feature>/<camelDomain>/<Verb><Domain>LocalDatasource.kt`

```kotlin
fun interface GetCompletedRecipesSinceLocalDatasource {
    suspend operator fun invoke(since: Long): LocalResult<List<CompletedRecipeCache>>
}

internal class GetCompletedRecipesSinceLocalDatasourceImpl(
    private val dispatchers: StovetimeDispatchers,
    private val completedRecipeDao: CompletedRecipeDao,
) : GetCompletedRecipesSinceLocalDatasource {
    override suspend fun invoke(since: Long): LocalResult<List<CompletedRecipeCache>> =
        safeDatabaseCall(dispatchers.io) {
            completedRecipeDao.getCompletedSince(since).map { it.toCache() }
        }
}
```

Writes return `LocalResult<Boolean>` and end the block with `true`. `Flow` operations map the entities and `catch` into the result type instead of letting the stream throw.

### 6. Parent aggregator — `sources/<feature>/<camelDomain>/<Domain>LocalDatasource.kt`

An interface with one `val` per operation + an `internal` Impl taking them as `override val` constructor params, the same shape as the network parent.

### 7. Koin — `di/LocalModule.kt`

- In `DaoModule`: `single<CompletedRecipeDao> { get<StovetimeDatabase>().completedRecipeDao }`
- New `internal val <Domain>LocalDatasourceModule` with `// parent` and `// children` `singleOf(...) { bind<...>() }` blocks.
- Add it to `SourcesModule.includes(...)`.

## Preferences (`:datasources:local`, DataStore)

Small key-value settings (keep screen on in cook mode, timer sound) go through a generic `PreferenceStore<T>` in `helpers/PreferenceStore.kt` (`save`, `get`, `observe`, `update`, `delete`), never a hand-written datasource per operation. The first preference creates `PreferenceStore`, `PreferencesFile` and `PreferencesDataStoreModule`. Progress is not a preference: XP, streak and completed recipes go in Room.

1. **Pick or add a group.** Related preferences share a group, and each group owns its keys and its own DataStore file: add an entry to `PreferencesFile` in `helpers/PreferencesDataStore.kt`. A preference unrelated to the others gets its own group. Never collect keys in one global object.
2. **One datasource per group**, one line per preference:

   ```kotlin
   interface CookSettingsLocalDatasource {
       val keepScreenOn: PreferenceStore<Boolean>
   }

   internal class CookSettingsLocalDatasourceImpl(
       dataStore: DataStore<Preferences>,
       dispatchers: StovetimeDispatchers,
   ) : CookSettingsLocalDatasource {
       override val keepScreenOn = dataStore.preference(booleanPreferencesKey("keep_screen_on"), dispatchers)
   }
   ```

   Values that belong together are one `@Serializable` object stored with `jsonPreference<T>("name", dispatchers)`, so they can't drift apart.
3. **Koin:** `PreferencesDataStoreModule` creates one DataStore per `PreferencesFile` (named by its `fileName`); bind the group datasource in `PreferencesLocalDatasourceModule` with `get(named(PreferencesFile.<Group>.fileName))`.
4. **Repository:** one per group in `:core:data` (`add-repository`), named `Get…`, `Observe…`, `Save…`, `Clear…`, applying defaults there. Test repositories with an in-memory `PreferenceStore`, and the store itself against real DataStore files.

## Rules

- One operation per `fun interface` with `suspend operator fun invoke`; grouping happens only in the parent aggregator.
- Impls are `internal`; inject `StovetimeDispatchers` and always run on `dispatchers.io` through `safeApiCall` / `safeDatabaseCall`.
- Never throw past the datasource boundary, but let `CancellationException` propagate (`safeApiCall` and `safeDatabaseCall` rethrow it).
- Don't import core:data types here; dependencies point downwards only (`datasources` know nothing about `core` except `core:common`).
- Add tests (see `generate-unit-test`): network sources use a Ktor `MockEngine` (`libs.ktor.client.mock`) + a test dispatchers object and cover success, `{"meals": null}`, HTTP error, `UnresolvedAddressException`, `SerializationException`, cancellation and generic exceptions. Assert the request path and query (`search.php?s=chicken`).
- Finish with the `checks` skill.

## Reference

None yet. When the first network and local datasources land, list their files here.
