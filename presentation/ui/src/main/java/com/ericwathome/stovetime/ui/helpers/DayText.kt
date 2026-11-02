package com.ericwathome.stovetime.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.TextStyle

@Composable
@ReadOnlyComposable
fun narrowDayName(date: LocalDate): String {
    val locale = LocalConfiguration.current.locales[0]
    return date.toJavaLocalDate().dayOfWeek.getDisplayName(TextStyle.NARROW, locale)
}
