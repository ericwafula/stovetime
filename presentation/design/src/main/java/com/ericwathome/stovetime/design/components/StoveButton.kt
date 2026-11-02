package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeLedge
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

enum class StoveButtonStyle { Primary, Success, Secondary }

@Composable
fun StoveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: StoveButtonStyle = StoveButtonStyle.Primary,
) {
    val colors = StovetimeTheme.colors
    val (face, ledge, content) =
        when (style) {
            StoveButtonStyle.Primary -> Triple(colors.primary, colors.primaryShade, colors.onAccent)
            StoveButtonStyle.Success -> Triple(colors.success, colors.successShade, colors.onAccent)
            StoveButtonStyle.Secondary -> Triple(colors.surface, colors.border, colors.textPrimary)
        }
    LedgeSurface(
        face = face,
        ledge = ledge,
        shape = StovetimeTheme.shapes.medium,
        ledgeHeight = StovetimeLedge.Regular,
        border = if (style == StoveButtonStyle.Secondary) colors.border else null,
        onClick = onClick,
        modifier = modifier,
    ) {
        StoveText(
            text = text,
            style = StovetimeTheme.typography.button,
            color = content,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 15.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun StoveButtonPreview() {
    StovetimePreviewTheme {
        Column(Modifier.padding(16.dp)) {
            StoveButton(text = "Start cooking", onClick = {}, modifier = Modifier.fillMaxWidth())
            StoveButton(text = "Continue", onClick = {}, style = StoveButtonStyle.Success, modifier = Modifier.padding(top = 12.dp))
            StoveButton(text = "Not now", onClick = {}, style = StoveButtonStyle.Secondary, modifier = Modifier.padding(top = 12.dp))
        }
    }
}
