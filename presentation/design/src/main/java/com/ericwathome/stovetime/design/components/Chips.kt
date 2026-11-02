package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeLedge
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

enum class StatKind { Streak, Xp }

@Composable
fun StatChip(
    kind: StatKind,
    value: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    val iconTint = if (kind == StatKind.Streak) colors.primary else colors.xp
    val color = if (kind == StatKind.Streak) colors.primaryContent else colors.xpContent
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        StoveIcon(if (kind == StatKind.Streak) StoveIcons.Flame else StoveIcons.Star, contentDescription, tint = iconTint)
        StoveText(value, style = StovetimeTheme.typography.heading, color = color)
    }
}

@Composable
fun TimerChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    LedgeSurface(
        face = colors.surface,
        ledge = colors.primaryShade,
        shape = StovetimeTheme.shapes.full,
        ledgeHeight = StovetimeLedge.Small,
        border = colors.primary,
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StoveIcon(StoveIcons.Timer, contentDescription = null, tint = colors.primaryContent, size = 20.dp)
            StoveText(text, style = StovetimeTheme.typography.label, color = colors.primaryContent)
        }
    }
}

@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = StovetimeTheme.colors.sunken,
    color: Color = StovetimeTheme.colors.textPrimary,
) {
    StoveText(
        text = text,
        style = StovetimeTheme.typography.label,
        color = color,
        modifier =
            modifier
                .background(background, StovetimeTheme.shapes.small)
                .padding(horizontal = 10.dp, vertical = 5.dp),
    )
}

@PreviewLightDark
@Composable
private fun ChipsPreview() {
    StovetimePreviewTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatChip(StatKind.Streak, value = "3", contentDescription = "Week streak")
                StatChip(StatKind.Xp, value = "240", contentDescription = "XP")
            }
            TimerChip(text = "Start 6:30", onClick = {})
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill("15 min")
                Pill("+30 XP", background = StovetimeTheme.colors.xpSoft, color = StovetimeTheme.colors.xpContent)
            }
        }
    }
}
