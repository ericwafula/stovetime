package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun BadgeTile(
    title: String,
    caption: String,
    earned: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    Column(
        modifier =
            modifier
                .background(if (earned) colors.xpSoft else colors.sunken, StovetimeTheme.shapes.medium)
                .clearAndSetSemantics { this.contentDescription = contentDescription }
                .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(if (earned) colors.xp else colors.lockedFill, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            StoveIcon(
                icon = if (earned) StoveIcons.Star else StoveIcons.Lock,
                contentDescription = null,
                tint = if (earned) colors.onXp else colors.lockedContent,
                size = 22.dp,
            )
        }
        StoveText(
            text = title,
            style = StovetimeTheme.typography.label,
            color = if (earned) colors.textPrimary else colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        StoveText(text = caption, style = StovetimeTheme.typography.caption, color = colors.textSecondary, textAlign = TextAlign.Center)
    }
}

@PreviewLightDark
@Composable
private fun BadgeTilePreview() {
    StovetimePreviewTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BadgeTile("Eggs done", "Every lesson in Crack it", earned = true, contentDescription = "", modifier = Modifier.weight(1f))
            BadgeTile("4-week streak", "Hit your goal 4 weeks in a row", earned = false, contentDescription = "", modifier = Modifier.weight(1f))
        }
    }
}
