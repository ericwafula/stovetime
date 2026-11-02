package com.ericwathome.stovetime.sdk.timer

internal data class AlarmChanges(
    val cancel: Set<String>,
    val schedule: Map<String, Long>,
)

internal fun alarmChanges(
    before: List<CookTimer>,
    after: List<CookTimer>,
): AlarmChanges {
    val wasScheduled = before.scheduledEnds()
    val shouldBeScheduled = after.scheduledEnds()
    return AlarmChanges(
        cancel = wasScheduled.keys - shouldBeScheduled.keys,
        schedule = shouldBeScheduled.filter { (id, endsAt) -> wasScheduled[id] != endsAt },
    )
}

private fun List<CookTimer>.scheduledEnds(): Map<String, Long> =
    filter { !it.ringing && it.endsAtMillis != null }.associate { it.id to it.endsAtMillis!! }

internal data class SoundPlan(
    val silenced: Set<String>,
    val stillSounding: Map<String, Long>,
) {
    val nextSilenceAt: Long?
        get() = stillSounding.values.minOrNull()
}

internal fun soundPlan(
    soundingUntil: Map<String, Long>,
    nowMillis: Long,
): SoundPlan {
    val silenced = soundingUntil.filterValues { it <= nowMillis }.keys
    return SoundPlan(silenced = silenced, stillSounding = soundingUntil - silenced)
}
