package com.ericwathome.stovetime.core.data.model.timers

import com.ericwathome.stovetime.core.data.model.progress.CookKind

sealed interface TimerTarget {
    val encoded: String

    data class CookStep(
        val kind: CookKind,
        val dishId: String,
        val startedAt: Long,
        val step: Int,
    ) : TimerTarget {
        override val encoded: String
            get() = listOf(COOK_PREFIX, kind.name, dishId, startedAt, step).joinToString(SEPARATOR)
    }

    data object Quick : TimerTarget {
        override val encoded: String = QUICK
    }

    companion object {
        private const val COOK_PREFIX = "cook"
        private const val QUICK = "quick"
        private const val SEPARATOR = "|"

        fun decode(encoded: String?): TimerTarget {
            val parts = encoded?.split(SEPARATOR).orEmpty()
            if (parts.size != 5 || parts[0] != COOK_PREFIX) return Quick
            val kind = CookKind.entries.firstOrNull { it.name == parts[1] } ?: return Quick
            val startedAt = parts[3].toLongOrNull() ?: return Quick
            val step = parts[4].toIntOrNull() ?: return Quick
            return CookStep(kind = kind, dishId = parts[2], startedAt = startedAt, step = step)
        }
    }
}
