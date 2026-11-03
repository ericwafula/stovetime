package com.ericwathome.stovetime.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ericwathome.stovetime.design.components.BadgeTile
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.ui.R

@Composable
fun BadgeCard(
    unitTitle: String?,
    streakWeeks: Int?,
    earned: Boolean,
    modifier: Modifier = Modifier,
) {
    val title =
        if (unitTitle != null) {
            stringResource(R.string.badge_unit_title)
        } else {
            stringResource(R.string.badge_streak_title, streakWeeks ?: 0)
        }
    val caption =
        if (unitTitle != null) {
            stringResource(R.string.badge_unit_caption, unitTitle)
        } else {
            stringResource(R.string.badge_streak_caption, streakWeeks ?: 0)
        }
    val state = stringResource(if (earned) R.string.badge_earned else R.string.badge_not_earned)
    BadgeTile(
        title = title,
        caption = caption,
        earned = earned,
        contentDescription = stringResource(R.string.badge_description, title, state, caption),
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun BadgeCardPreview() {
    StovetimePreviewTheme {
        BadgeCard(unitTitle = "Crack it: five ways with eggs", streakWeeks = null, earned = true)
    }
}
