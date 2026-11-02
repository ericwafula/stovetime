package com.ericwathome.stovetime.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private fun missingTheme(): Nothing = error("No StovetimeTheme found. Wrap your content in StovetimeTheme { … }.")

internal val LocalStovetimeColors = staticCompositionLocalOf<StovetimeColors> { missingTheme() }
internal val LocalStovetimeTypography = staticCompositionLocalOf<StovetimeTypography> { missingTheme() }
internal val LocalStovetimeSpacing = staticCompositionLocalOf<StovetimeSpacing> { missingTheme() }
internal val LocalStovetimeShapes = staticCompositionLocalOf<StovetimeShapes> { missingTheme() }

@Composable
fun StovetimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    CompositionLocalProvider(
        LocalStovetimeColors provides colors,
        LocalStovetimeTypography provides DefaultTypography,
        LocalStovetimeSpacing provides StovetimeSpacing(),
        LocalStovetimeShapes provides StovetimeShapes(),
        LocalTextSelectionColors provides TextSelectionColors(colors.primary, colors.primarySoft),
        content = content,
    )
}

object StovetimeTheme {
    val colors: StovetimeColors
        @Composable @ReadOnlyComposable
        get() = LocalStovetimeColors.current

    val typography: StovetimeTypography
        @Composable @ReadOnlyComposable
        get() = LocalStovetimeTypography.current

    val spacing: StovetimeSpacing
        @Composable @ReadOnlyComposable
        get() = LocalStovetimeSpacing.current

    val shapes: StovetimeShapes
        @Composable @ReadOnlyComposable
        get() = LocalStovetimeShapes.current
}
