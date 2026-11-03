package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun StoveTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = StovetimeTheme.colors.primaryContent,
) {
    Box(
        modifier =
            modifier
                .heightIn(min = 48.dp)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        StoveText(text = text, style = StovetimeTheme.typography.label, color = color)
    }
}

@PreviewLightDark
@Composable
private fun StoveTextButtonPreview() {
    StovetimePreviewTheme {
        StoveTextButton(text = "Start over", onClick = {}, modifier = Modifier.padding(16.dp))
    }
}
