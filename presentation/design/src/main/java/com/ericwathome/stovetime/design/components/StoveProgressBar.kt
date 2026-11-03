package com.ericwathome.stovetime.design.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun StoveProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = StovetimeTheme.colors.success,
    track: Color = StovetimeTheme.colors.sunken,
    height: Dp = 14.dp,
) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), label = "progress")
    val shape = RoundedCornerShape(height / 2)
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .background(track, shape),
    ) {
        Box(
            Modifier
                .fillMaxWidth(animated)
                .fillMaxHeight()
                .background(color, shape),
        ) {
            Box(
                Modifier
                    .padding(start = 10.dp, end = 10.dp, top = height / 4)
                    .fillMaxWidth()
                    .height(height / 4)
                    .background(StovetimeTheme.colors.onAccent.copy(alpha = 0.35f), shape),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StoveProgressBarPreview() {
    StovetimePreviewTheme {
        StoveProgressBar(progress = 0.4f, modifier = Modifier.padding(16.dp))
    }
}
