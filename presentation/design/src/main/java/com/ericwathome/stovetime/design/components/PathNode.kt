package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeLedge
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

enum class PathNodeState { Done, Current, Locked }

@Composable
fun PathNode(
    state: PathNodeState,
    contentDescription: String,
    modifier: Modifier = Modifier,
    photo: Painter? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = StovetimeTheme.colors
    val (face, ledge) =
        when (state) {
            PathNodeState.Done -> colors.success to colors.successShade
            PathNodeState.Current -> colors.primary to colors.primaryShade
            PathNodeState.Locked -> colors.lockedFill to colors.border
        }
    LedgeSurface(
        face = face,
        ledge = ledge,
        shape = CircleShape,
        ledgeHeight = StovetimeLedge.Large,
        onClick = onClick,
        modifier = modifier.size(width = 76.dp, height = 82.dp),
    ) {
        when (state) {
            PathNodeState.Done -> StoveIcon(StoveIcons.Check, contentDescription, tint = colors.onAccent, size = 34.dp)
            PathNodeState.Locked -> StoveIcon(StoveIcons.Lock, contentDescription, tint = colors.lockedContent, size = 34.dp)
            PathNodeState.Current ->
                Image(
                    painter = photo ?: ColorPainter(colors.primarySoft),
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(5.dp, colors.primary, CircleShape),
                )
        }
    }
}

@PreviewLightDark
@Composable
private fun PathNodePreview() {
    StovetimePreviewTheme {
        Row(Modifier.padding(16.dp)) {
            PathNode(PathNodeState.Done, contentDescription = "Boiled eggs, cooked", onClick = {})
            PathNode(PathNodeState.Current, contentDescription = "Jammy eggs, up next", onClick = {}, modifier = Modifier.padding(start = 16.dp))
            PathNode(PathNodeState.Locked, contentDescription = "French omelette, locked", modifier = Modifier.padding(start = 16.dp))
        }
    }
}
