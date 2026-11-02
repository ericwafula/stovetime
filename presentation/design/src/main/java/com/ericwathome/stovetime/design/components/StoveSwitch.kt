package com.ericwathome.stovetime.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

private val TrackWidth = 48.dp
private val TrackHeight = 28.dp
private val ThumbSize = 22.dp
private val ThumbInset = 3.dp

@Composable
fun StoveSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    val track by animateColorAsState(if (checked) colors.success else colors.border, label = "track")
    val thumbOffset by animateDpAsState(if (checked) TrackWidth - ThumbSize - ThumbInset else ThumbInset, label = "thumb")
    val toggle =
        if (onCheckedChange != null) {
            Modifier.toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
        } else {
            Modifier
        }
    Box(
        modifier =
            modifier
                .then(toggle)
                .size(TrackWidth, TrackHeight)
                .background(track, CircleShape),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = thumbOffset)
                .size(ThumbSize)
                .background(colors.onAccent, CircleShape),
        )
    }
}

@PreviewLightDark
@Composable
private fun StoveSwitchPreview() {
    StovetimePreviewTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StoveSwitch(checked = true, onCheckedChange = {})
            StoveSwitch(checked = false, onCheckedChange = {})
        }
    }
}
