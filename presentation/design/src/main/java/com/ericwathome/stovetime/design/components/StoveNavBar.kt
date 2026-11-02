package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeLedge
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

data class StoveNavItem(
    val icon: StoveIcons,
    val label: String,
)

val StoveNavBarClearance: Dp = 112.dp

@Composable
fun StoveNavBar(
    items: List<StoveNavItem>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    LedgeSurface(
        face = colors.surface,
        ledge = colors.border,
        shape = StovetimeTheme.shapes.large,
        ledgeHeight = 5.dp,
        border = colors.border,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items.forEachIndexed { index, item ->
                NavTab(item = item, active = index == selected, onClick = { onSelect(index) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun NavTab(
    item: StoveNavItem,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    val content = if (active) colors.onAccent else colors.textSecondary
    LedgeSurface(
        face = if (active) colors.primary else colors.surface,
        ledge = if (active) colors.primaryShade else colors.surface,
        shape = StovetimeTheme.shapes.medium,
        ledgeHeight = StovetimeLedge.Regular,
        onClick = onClick,
        modifier = modifier.height(60.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StoveIcon(item.icon, contentDescription = null, tint = content, size = 22.dp)
            StoveText(
                text = item.label,
                style = if (active) StovetimeTheme.typography.label else StovetimeTheme.typography.caption,
                color = content,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StoveNavBarPreview() {
    StovetimePreviewTheme {
        StoveNavBar(
            items =
                listOf(
                    StoveNavItem(StoveIcons.Home, "Path"),
                    StoveNavItem(StoveIcons.Book, "Recipes"),
                    StoveNavItem(StoveIcons.Timer, "Timers"),
                    StoveNavItem(StoveIcons.User, "Me"),
                ),
            selected = 0,
            onSelect = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
