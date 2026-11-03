package com.ericwathome.stovetime.ui.helpers

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.ericwathome.stovetime.core.common.helpers.RelativeDay
import com.ericwathome.stovetime.core.common.helpers.relativeDay
import com.ericwathome.stovetime.ui.R
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toJavaLocalDate
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
@ReadOnlyComposable
fun narrowDayName(date: LocalDate): String {
    val locale = LocalConfiguration.current.locales[0]
    return date.toJavaLocalDate().dayOfWeek.getDisplayName(TextStyle.NARROW, locale)
}

@Composable
@ReadOnlyComposable
fun dayText(
    date: LocalDate,
    today: LocalDate,
): String {
    val locale = LocalConfiguration.current.locales[0]
    return when (val day = relativeDay(date, today)) {
        RelativeDay.Today -> stringResource(R.string.day_today)
        RelativeDay.Yesterday -> stringResource(R.string.day_yesterday)
        is RelativeDay.ThisWeek -> DayOfWeek.of(day.dayOfWeek.isoDayNumber).getDisplayName(TextStyle.FULL_STANDALONE, locale)
        is RelativeDay.Earlier -> day.date.format(skeleton = if (day.isThisYear) "MMMd" else "yMMMd", locale = locale)
    }
}

@Composable
@ReadOnlyComposable
fun monthText(
    date: LocalDate,
    today: LocalDate,
): String {
    val locale = LocalConfiguration.current.locales[0]
    return date.format(skeleton = if (date.year == today.year) "LLLL" else "LLLLy", locale = locale)
}

private fun LocalDate.format(
    skeleton: String,
    locale: Locale,
): String = toJavaLocalDate().format(DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, skeleton), locale))
