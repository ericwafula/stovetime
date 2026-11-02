---
name: add-worker
description: Add background work to Stovetime with WorkManager — a CoroutineWorker in :core:data with Koin constructor injection (workerOf + workManagerFactory), Context.start*/schedule* enqueue extensions with unique work names and explicit policies, constraints/backoff, a retry-then-fail cap, a scheduler fun-interface so ViewModels never touch WorkManager, and tests. Also says when a cook timer is not a worker.
when_to_use: User asks for background sync or prefetch (e.g. download upcoming path recipes for offline cook mode), periodic or one-off jobs, "run X when online", "refresh every N hours", a reminder at a time of day, or anything that must survive the app being closed. Also when touching any existing *Worker, or deciding how a cook timer runs in the background.
---

# Skill: add-worker

Workers are the app's background entry points. They contain **no business logic**: they read input, call a repository or use case, and translate its `DataResult` into a WorkManager `Result`. Everything lives in `:core:data` under `com.ericwathome.stovetime.core.data.workers.<feature>`.

## Decide first

| Need | Request | Enqueue with | Policy |
|---|---|---|---|
| Run once now (prefetch after a path node unlocks) | `OneTimeWorkRequestBuilder` | `enqueueUniqueWork` | `KEEP` (ignore duplicates) or `APPEND_OR_REPLACE` (queue a follow-up run) |
| Run once, must start ASAP and is short | `OneTimeWorkRequestBuilder` + `setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)` | `enqueueUniqueWork` | `KEEP` |
| Repeat (refresh cached recipes) | `PeriodicWorkRequestBuilder` (**min 15 minutes**) | `enqueueUniquePeriodicWork` | `UPDATE` (re-schedule safely with new params) or `KEEP` |
| At a time of day (streak reminder before the week ends) | periodic `1, TimeUnit.DAYS` + `setInitialDelay(untilTarget)` | `enqueueUniquePeriodicWork` | `UPDATE` |

- Always use **unique** work. A plain `enqueue` duplicates work every time the trigger fires.
- Anything under 15 minutes, or tied to a visible screen, is a coroutine in the ViewModel, not a worker.
- **Cook timers are not workers.** A step timer must ring on the second, and WorkManager only promises "roughly then". A running timer that has to survive the app being backgrounded is a foreground service with an ongoing notification (and an exact alarm for the finish), owned by the timer engine. If a task asks for a timer through WorkManager, flag it to the user.

## One-time setup (first worker only)

Check these before writing the first worker. Skip the ones already done.

1. **Disable the default initializer** so Koin's factory is used. Without this WorkManager initialises itself with the default factory and constructor-injected workers crash with `NoSuchMethodException`. In `app/src/main/AndroidManifest.xml`, add `xmlns:tools="http://schemas.android.com/tools"` to `<manifest>` if it isn't there, then inside `<application>`:

   ```xml
   <provider
       android:name="androidx.startup.InitializationProvider"
       android:authorities="${applicationId}.androidx-startup"
       android:exported="false"
       tools:node="merge">
       <meta-data
           android:name="androidx.work.WorkManagerInitializer"
           android:value="androidx.startup"
           tools:node="remove" />
   </provider>
   ```

2. **Koin factory.** In `StovetimeApplication.onCreate()`, add `workManagerFactory()` inside the `startKoin { … }` block, after `androidContext(...)`. `:app` needs `implementation(libs.koin.androidx.workmanager)` for it.

3. **Dependencies** in `core/data/build.gradle.kts`:

   ```kotlin
   // libraries
   implementation(libs.work.manager)
   implementation(libs.koin.androidx.workmanager)
   ```

   The Koin BOM comes from the convention plugin, so no version is needed.

4. **Retry cap helper.** Create `core/data/.../helpers/WorkerHelpers.kt`:

   ```kotlin
   internal const val MAX_WORKER_ATTEMPTS = 3

   internal fun workerResult(runAttemptCount: Int): ListenableWorker.Result =
       if (runAttemptCount + 1 >= MAX_WORKER_ATTEMPTS) ListenableWorker.Result.failure() else ListenableWorker.Result.retry()

   internal fun <T> DataResult<T>.toWorkerResult(runAttemptCount: Int): ListenableWorker.Result =
       when (this) {
           is DataResult.Success -> ListenableWorker.Result.success()
           is DataResult.Error -> workerResult(runAttemptCount)
       }
   ```

   It retries with backoff until the cap, then gives up so a poisoned job can't loop forever. It takes the raw attempt count, not `WorkerParameters`, so it's unit-testable without WorkManager.

5. **Koin module.** Create `core/data/.../workers/WorkerModule.kt`:

   ```kotlin
   internal val WorkerModule =
       module {
           // workerOf(::<Name>Worker)
       }
   ```

   Add `WorkerModule` to `CoreDataModule.includes(...)` in `di/CoreDataModule.kt`.

## Files per worker

### 1. Worker — `workers/<feature>/<Name>Worker.kt`

```kotlin
const val PREFETCH_PATH_RECIPES_WORK_NAME = "PrefetchPathRecipesWorker"

class PrefetchPathRecipesWorker(
    context: Context,
    params: WorkerParameters,
    private val recipesRepository: RecipesRepository,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val count = inputData.getInt(KEY_COUNT, 0)
        if (count <= 0) return Result.failure()

        return recipesRepository
            .prefetchUpcomingPathRecipes(count = count)
            .toWorkerResult(runAttemptCount)
    }

    companion object {
        const val KEY_COUNT = "count"
    }
}
```

- Constructor-inject collaborators (registered with `workerOf`). Don't use `KoinJavaComponent.inject` or `GlobalContext.get()`: it hides dependencies and makes tests stand up Koin.
- Don't wrap in `withContext(Dispatchers.IO)`. `CoroutineWorker` already runs off the main thread, and datasources switch to `dispatchers.io` themselves.
- Missing or invalid input is a programming error, so return `failure()`, not `retry()`.
- Keep `doWork` short. If it needs branching logic, move that into a use case (`add-usecase`) or a top-level `internal suspend fun` seam that takes plain arguments and returns `Result`. The seam is what you unit-test.
- Input/output data is limited to 10 KB of primitives. Pass ids or counts, never models. The worker re-reads anything else from the repository.

### 2. Enqueue extensions — same file, above the class

```kotlin
fun Context.startPrefetchPathRecipesWork(count: Int) {
    val request =
        OneTimeWorkRequestBuilder<PrefetchPathRecipesWorker>()
            .setInputData(workDataOf(PrefetchPathRecipesWorker.KEY_COUNT to count))
            .setConstraints(networkConstraints())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .addTag(PREFETCH_PATH_RECIPES_WORK_NAME)
            .build()

    WorkManager
        .getInstance(this)
        .enqueueUniqueWork(PREFETCH_PATH_RECIPES_WORK_NAME, ExistingWorkPolicy.KEEP, request)
}

fun Context.schedulePeriodicPrefetchPathRecipesWork(count: Int) {
    val request =
        PeriodicWorkRequestBuilder<PrefetchPathRecipesWorker>(Duration.ofDays(1))
            .setInputData(workDataOf(PrefetchPathRecipesWorker.KEY_COUNT to count))
            .setConstraints(networkConstraints())
            .addTag(PREFETCH_PATH_RECIPES_WORK_NAME)
            .build()

    WorkManager
        .getInstance(this)
        .enqueueUniquePeriodicWork(PREFETCH_PATH_RECIPES_WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
}

fun Context.cancelPrefetchPathRecipesWork() {
    WorkManager.getInstance(this).cancelUniqueWork(PREFETCH_PATH_RECIPES_WORK_NAME)
}

private fun networkConstraints(): Constraints =
    Constraints
        .Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
```

A one-time and a periodic request for the same worker need different unique names if both can be live at once. Move `networkConstraints()` into `WorkerHelpers.kt` once a second worker needs it.

### 3. Scheduler — `workers/<feature>/<Name>WorkScheduler.kt`

ViewModels and use cases must not hold a `Context` or import `androidx.work`. Expose a `fun interface` instead:

```kotlin
fun interface SchedulePrefetchPathRecipesWork {
    operator fun invoke(count: Int)
}

internal class SchedulePrefetchPathRecipesWorkImpl(
    private val context: Context,
) : SchedulePrefetchPathRecipesWork {
    override fun invoke(count: Int) = context.startPrefetchPathRecipesWork(count = count)
}
```

Bind it next to the worker in `WorkerModule`:

```kotlin
workerOf(::PrefetchPathRecipesWorker)
singleOf(::SchedulePrefetchPathRecipesWorkImpl) {
    bind<SchedulePrefetchPathRecipesWork>()
}
```

`Context` resolves from `androidContext()` set in `StovetimeApplication`. App-wide schedules that should always be on are a startup step wired from `:app` after Koin starts, not a coroutine launched in `StovetimeApplication`.

### 4. Observing status (optional)

When the UI should reflect a sync ("Downloading recipes for offline"), expose a `Flow` from a repository rather than from the ViewModel:

```kotlin
WorkManager.getInstance(context)
    .getWorkInfosForUniqueWorkFlow(PREFETCH_PATH_RECIPES_WORK_NAME)
    .map { infos -> infos.any { it.state == WorkInfo.State.RUNNING } }
```

## Tests

- **Result mapping and seams** go in plain JUnit4 tests in `core/data/src/test/.../workers/`, with no WorkManager or Robolectric. Cover success → `success()`, an error below the cap → `retry()`, an error at the cap → `failure()`, and invalid input → `failure()`.
- **The worker end to end** can use `TestListenableWorkerBuilder<PrefetchPathRecipesWorker>(context).setWorkerFactory(...)`, or construct the worker directly with a mocked `WorkerParameters`. This needs Robolectric (not in the catalog yet) and `libs.work.testing` (already in the catalog). Add Robolectric to `gradle/libs.versions.toml` and `core/data` `testImplementation` only when a test needs it. Tell the user you're adding it.
- **Enqueueing.** Under Robolectric, `WorkManagerTestInitHelper.initializeTestWorkManager(context, Configuration.Builder().setExecutor(SynchronousExecutor()).setWorkerFactory(noOpFactory).build())`, call the extension, then assert `WorkManager.getInstance(context).getWorkInfosForUniqueWork(PREFETCH_PATH_RECIPES_WORK_NAME).get()`. The no-op factory stops enqueued workers from running against real Koin.

## Rules

- Workers live only in `:core:data`. Features and `:core:domain` never import `androidx.work`.
- One worker = one job. Chain with `beginUniqueWork(...).then(...)` rather than doing two jobs in one `doWork`.
- Unique work name = the `const val` next to the worker. Use the same string for the tag.
- Always set an explicit `Existing*WorkPolicy`, and explain the choice in a one-line comment when it isn't `KEEP`.
- Network-dependent work (anything that calls TheMealDB) sets `NetworkType.CONNECTED`. Don't check connectivity inside `doWork`.
- Never swallow `CancellationException`. WorkManager cancels workers by cancelling the coroutine.
- Finish with the `checks` skill.

## Notifications from a worker

There is no notification module yet. Workers never build Android notifications inline; if a worker needs to post one (a streak reminder), stop and agree with the user where notification code lives before writing it.

## Reference

`core/data/.../workers/recipes/RecipeSyncWorker.kt` (daily unique periodic work plus a one-time run, both `NetworkType.CONNECTED` with exponential backoff), its schedulers in `RecipeSyncWorkScheduler.kt` (`ScheduleRecipeSyncWork`, `StartRecipeSyncWork`, `ObserveRecipeSyncRunning`), `WorkerModule`, and `WorkerHelpersTest` / `RecipeSyncRunningTest`.
