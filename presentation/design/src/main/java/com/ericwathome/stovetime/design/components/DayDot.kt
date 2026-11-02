package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun DayDot(
    label: String,
    cooked: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier =
                Modifier
                    .size(34.dp)
                    .background(if (cooked) colors.primary else colors.sunken, CircleShape)
                    .then(if (isToday) Modifier.border(3.dp, colors.primaryShade, CircleShape) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            if (cooked) StoveIcon(StoveIcons.Flame, contentDescription = null, tint = colors.onAccent, size = 16.dp)
        }
        StoveText(text = label, style = StovetimeTheme.typography.caption, color = colors.textSecondary)
    }
}

@PreviewLightDark
@Composable
private fun DayDotPreview() {
    StovetimePreviewTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DayDot(label = "M", cooked = true, isToday = false)
            DayDot(label = "T", cooked = false, isToday = false)
            DayDot(label = "F", cooked = true, isToday = true)
            DayDot(label = "S", cooked = false, isToday = true)
        }
    }
}
