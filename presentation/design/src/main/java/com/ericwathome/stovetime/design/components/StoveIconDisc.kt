package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeLedge
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun StoveIconDisc(
    icon: StoveIcons,
    modifier: Modifier = Modifier,
    face: Color = StovetimeTheme.colors.surface,
    ledge: Color = StovetimeTheme.colors.primaryShade,
    tint: Color = StovetimeTheme.colors.primary,
    size: Dp = 128.dp,
    iconSize: Dp = 60.dp,
) {
    LedgeSurface(
        face = face,
        ledge = ledge,
        shape = StovetimeTheme.shapes.full,
        ledgeHeight = StovetimeLedge.Large,
        modifier = modifier.size(width = size, height = size + StovetimeLedge.Large),
    ) {
        StoveIcon(icon = icon, contentDescription = null, tint = tint, size = iconSize)
    }
}

@PreviewLightDark
@Composable
private fun StoveIconDiscPreview() {
    StovetimePreviewTheme(background = { it.primary }) {
        StoveIconDisc(icon = StoveIcons.Timer, modifier = Modifier.padding(16.dp))
    }
}
