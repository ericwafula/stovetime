package com.ericwathome.stovetime.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import com.ericwathome.stovetime.MainActivity
import com.ericwathome.stovetime.R
import com.ericwathome.stovetime.core.common.helpers.timerText
import com.ericwathome.stovetime.sdk.timer.StovetimeTimers

class TimersWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, render(context)) }
    }

    companion object {
        private val rowIds =
            listOf(
                Triple(R.id.widget_row_0, R.id.widget_name_0, R.id.widget_time_0) to R.id.widget_status_0,
                Triple(R.id.widget_row_1, R.id.widget_name_1, R.id.widget_time_1) to R.id.widget_status_1,
                Triple(R.id.widget_row_2, R.id.widget_name_2, R.id.widget_time_2) to R.id.widget_status_2,
            )

        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, TimersWidgetProvider::class.java))
            if (ids.isEmpty()) return
            val views = render(context)
            ids.forEach { manager.updateAppWidget(it, views) }
        }

        private fun render(context: Context): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_timers)
            val rows = widgetRows(StovetimeTimers.engine.timers.value, System.currentTimeMillis())
            views.setViewVisibility(R.id.widget_empty, if (rows.isEmpty()) View.VISIBLE else View.GONE)
            views.setOnClickPendingIntent(R.id.widget_quick_timer, openApp(context, OPEN_TIMERS_PAYLOAD, requestCode = 0))
            rowIds.forEachIndexed { index, (ids, statusId) ->
                val (rowId, nameId, timeId) = ids
                val row = rows.getOrNull(index)
                if (row == null) {
                    views.setViewVisibility(rowId, View.GONE)
                    return@forEachIndexed
                }
                views.setViewVisibility(rowId, View.VISIBLE)
                views.setTextViewText(nameId, row.name)
                views.setOnClickPendingIntent(rowId, openApp(context, row.payload, requestCode = index + 1))
                when (val state = row.state) {
                    is WidgetRowState.Running -> {
                        views.setViewVisibility(timeId, View.VISIBLE)
                        views.setViewVisibility(statusId, View.GONE)
                        views.setChronometer(timeId, SystemClock.elapsedRealtime() + state.remainingMillis, null, true)
                        views.setChronometerCountDown(timeId, true)
                    }

                    is WidgetRowState.Paused -> {
                        views.setViewVisibility(timeId, View.GONE)
                        views.setViewVisibility(statusId, View.VISIBLE)
                        views.setTextViewText(statusId, context.getString(R.string.widget_paused, timerText(state.remainingMillis)))
                    }

                    WidgetRowState.TimeUp -> {
                        views.setViewVisibility(timeId, View.GONE)
                        views.setViewVisibility(statusId, View.VISIBLE)
                        views.setTextViewText(statusId, context.getString(R.string.widget_times_up))
                    }
                }
            }
            return views
        }

        private fun openApp(
            context: Context,
            payload: String,
            requestCode: Int,
        ): PendingIntent =
            PendingIntent.getActivity(
                context,
                requestCode,
                MainActivity.timerIntent(context, payload),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
    }
}
