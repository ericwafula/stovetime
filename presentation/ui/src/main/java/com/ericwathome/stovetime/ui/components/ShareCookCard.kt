package com.ericwathome.stovetime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.components.StatTile
import com.ericwathome.stovetime.design.components.StatTone
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.ui.R
import com.ericwathome.stovetime.ui.helpers.cookTimeText
import com.ericwathome.stovetime.ui.helpers.countText

@Composable
fun ShareCookCard(
    name: String,
    photo: String?,
    xpEarned: Int,
    cookMillis: Long,
    weekStreak: Int,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    val colors = StovetimeTheme.colors
    Column(
        modifier
            .width(360.dp)
            .background(colors.canvas, StovetimeTheme.shapes.large)
            .padding(spacing.s24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(136.dp)
                .border(6.dp, colors.success, CircleShape)
                .padding(9.dp)
                .clip(CircleShape),
        ) {
            DishPhoto(photo = photo, contentDescription = null, modifier = Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(spacing.s16))
        StoveText(text = name, style = StovetimeTheme.typography.title, textAlign = TextAlign.Center)
        Spacer(Modifier.height(spacing.s8))
        StoveText(
            text = stringResource(R.string.share_cook_caption),
            style = StovetimeTheme.typography.label,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(spacing.s20))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s8), modifier = Modifier.fillMaxWidth()) {
            StatTile(
                value = stringResource(R.string.share_cook_xp_value, xpEarned),
                label = stringResource(R.string.share_cook_xp_label),
                tone = StatTone.Xp,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                value = cookTimeText(cookMillis),
                label = stringResource(R.string.share_cook_time_label),
                tone = StatTone.Success,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                value = countText(weekStreak),
                label = stringResource(R.string.share_cook_streak_label),
                tone = StatTone.Primary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ShareCookCardPreview() {
    StovetimePreviewTheme {
        ShareCookCard(name = "Jammy eggs on toast", photo = "lesson_jammy_eggs_on_toast", xpEarned = 30, cookMillis = 840_000, weekStreak = 3)
    }
}
