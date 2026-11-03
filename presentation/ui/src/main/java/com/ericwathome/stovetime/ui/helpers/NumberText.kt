package com.ericwathome.stovetime.ui.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.text.NumberFormat

@Composable
@ReadOnlyComposable
fun countText(count: Int): String {
    val locale = LocalConfiguration.current.locales[0]
    return NumberFormat.getIntegerInstance(locale).format(count)
}
