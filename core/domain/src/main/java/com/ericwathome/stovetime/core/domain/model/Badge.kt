package com.ericwathome.stovetime.core.domain.model

sealed interface BadgeKind {
    data class UnitDone(
        val courseTitle: String,
        val unitTitle: String,
    ) : BadgeKind

    data class Streak(
        val weeks: Int,
    ) : BadgeKind
}

data class Badge(
    val id: String,
    val kind: BadgeKind,
    val earned: Boolean,
)
