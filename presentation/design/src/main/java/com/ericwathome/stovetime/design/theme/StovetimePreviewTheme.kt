package com.ericwathome.stovetime.design.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun StovetimePreviewTheme(
    background: (StovetimeColors) -> Color = { it.canvas },
    content: @Composable () -> Unit,
) {
    StovetimeTheme {
        Box(Modifier.background(background(StovetimeTheme.colors))) { content() }
    }
}
