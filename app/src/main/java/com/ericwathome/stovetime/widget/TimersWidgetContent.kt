package com.ericwathome.stovetime.widget

import com.ericwathome.stovetime.sdk.timer.CookTimer

internal const val WIDGET_MAX_ROWS = 3

internal sealed interface WidgetRowState {
    data class Running(
        val remainingMillis: Long,
    ) : WidgetRowState

    data class Paused(
        val remainingMillis: Long,
    ) : WidgetRowState

    data object TimeUp : WidgetRowState
}

internal data class WidgetRow(
    val timerId: String,
    val name: String,
    val state: WidgetRowState,
    val payload: String,
)

private val widgetOrder =
    compareBy<CookTimer>(
        { !it.ringing },
        { it.isPaused },
        { it.endsAtMillis ?: Long.MAX_VALUE },
        { it.pausedRemainingMillis ?: Long.MAX_VALUE },
    )

internal fun widgetRows(
    timers: List<CookTimer>,
    nowMillis: Long,
): List<WidgetRow> =
    timers
        .sortedWith(widgetOrder)
        .take(WIDGET_MAX_ROWS)
        .map { timer ->
            WidgetRow(
                timerId = timer.id,
                name = timer.name,
                state =
                    when {
                        timer.ringing -> WidgetRowState.TimeUp
                        timer.isPaused -> WidgetRowState.Paused(timer.remainingMillis(nowMillis))
                        else -> WidgetRowState.Running(timer.remainingMillis(nowMillis))
                    },
                payload = timer.payload.orEmpty(),
            )
        }

internal const val OPEN_TIMERS_PAYLOAD = ""
