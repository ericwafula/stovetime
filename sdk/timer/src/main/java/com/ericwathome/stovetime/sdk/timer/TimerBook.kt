package com.ericwathome.stovetime.sdk.timer

import java.util.UUID
import kotlin.time.Duration.Companion.minutes
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

internal class TimerBook(
    restored: List<CookTimer> = emptyList(),
    private val newId: () -> String = { UUID.randomUUID().toString() },
) {
    var timers: List<CookTimer> = restored.sortedWith(order)
        private set

    fun start(
        name: String,
        durationMillis: Long,
        detail: String?,
        payload: String?,
        nowMillis: Long,
    ): CookTimer {
        val timer =
            CookTimer(
                id = newId(),
                name = name,
                detail = detail,
                durationMillis = durationMillis,
                endsAtMillis = nowMillis + durationMillis,
                pausedRemainingMillis = null,
                payload = payload,
                ringing = false,
            )
        timers = (timers + timer).sortedWith(order)
        return timer
    }

    fun pause(
        id: String,
        nowMillis: Long,
    ) = edit(id) { timer ->
        val remaining = timer.remainingMillis(nowMillis)
        if (timer.isPaused || timer.ringing || remaining == 0L) {
            timer
        } else {
            timer.copy(endsAtMillis = null, pausedRemainingMillis = remaining)
        }
    }

    fun resume(
        id: String,
        nowMillis: Long,
    ) = edit(id) { timer ->
        timer.pausedRemainingMillis?.let { timer.copy(endsAtMillis = nowMillis + it, pausedRemainingMillis = null) } ?: timer
    }

    fun addMinute(
        id: String,
        nowMillis: Long,
    ) = edit(id) { timer ->
        when {
            timer.ringing -> timer.restartedForAMinute(nowMillis)
            timer.isPaused ->
                timer.copy(
                    pausedRemainingMillis = timer.pausedRemainingMillis!! + ONE_MINUTE,
                    durationMillis = timer.durationMillis + ONE_MINUTE,
                )
            else ->
                timer.copy(
                    endsAtMillis = timer.endsAtMillis!! + ONE_MINUTE,
                    durationMillis = timer.durationMillis + ONE_MINUTE,
                )
        }
    }

    fun snooze(
        id: String,
        nowMillis: Long,
    ) = edit(id) { it.restartedForAMinute(nowMillis) }

    fun cancel(id: String) {
        timers = timers.filterNot { it.id == id }
    }

    fun stopRinging(id: String) = cancel(id)

    fun ringDue(nowMillis: Long): List<CookTimer> {
        val due = timers.filter { !it.ringing && it.endsAtMillis != null && it.endsAtMillis <= nowMillis }.map { it.id }.toSet()
        timers = timers.map { if (it.id in due) it.copy(ringing = true) else it }.sortedWith(order)
        return timers.filter { it.id in due }
    }

    fun toJson(): String = json.encodeToString(timers.map(StoredTimer::from))

    private fun edit(
        id: String,
        change: (CookTimer) -> CookTimer,
    ) {
        timers = timers.map { if (it.id == id) change(it) else it }.sortedWith(order)
    }

    private fun CookTimer.restartedForAMinute(nowMillis: Long) =
        copy(
            durationMillis = ONE_MINUTE,
            endsAtMillis = nowMillis + ONE_MINUTE,
            pausedRemainingMillis = null,
            ringing = false,
        )

    companion object {
        val ONE_MINUTE = 1.minutes.inWholeMilliseconds

        private val json = Json { ignoreUnknownKeys = true }

        private val order =
            compareBy<CookTimer>(
                { it.isPaused },
                { it.endsAtMillis ?: Long.MAX_VALUE },
                { it.pausedRemainingMillis ?: 0L },
            )

        fun fromJson(stored: String?): TimerBook {
            val timers =
                try {
                    stored?.let { json.decodeFromString<List<StoredTimer>>(it) }.orEmpty().mapNotNull(StoredTimer::toCookTimer)
                } catch (_: SerializationException) {
                    emptyList()
                } catch (_: IllegalArgumentException) {
                    emptyList()
                }
            return TimerBook(timers)
        }
    }
}

@Serializable
private data class StoredTimer(
    val id: String,
    val name: String,
    val detail: String? = null,
    val durationMillis: Long,
    val endsAtMillis: Long? = null,
    val pausedRemainingMillis: Long? = null,
    val payload: String? = null,
    val ringing: Boolean = false,
) {
    fun toCookTimer(): CookTimer? =
        runCatching {
            CookTimer(id, name, detail, durationMillis, endsAtMillis, pausedRemainingMillis, payload, ringing)
        }.getOrNull()

    companion object {
        fun from(timer: CookTimer) =
            StoredTimer(
                id = timer.id,
                name = timer.name,
                detail = timer.detail,
                durationMillis = timer.durationMillis,
                endsAtMillis = timer.endsAtMillis,
                pausedRemainingMillis = timer.pausedRemainingMillis,
                payload = timer.payload,
                ringing = timer.ringing,
            )
    }
}
