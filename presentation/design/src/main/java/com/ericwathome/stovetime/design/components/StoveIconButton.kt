package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun StoveIconButton(
    icon: StoveIcons,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(48.dp)
                .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .background(StovetimeTheme.colors.surface, StovetimeTheme.shapes.full),
            contentAlignment = Alignment.Center,
        ) {
            StoveIcon(icon = icon, contentDescription = contentDescription, size = 20.dp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF7A6857)
@Composable
private fun StoveIconButtonPreview() {
    StovetimeTheme {
        StoveIconButton(icon = StoveIcons.Close, contentDescription = "Close", onClick = {}, modifier = Modifier.padding(16.dp))
    }
}
