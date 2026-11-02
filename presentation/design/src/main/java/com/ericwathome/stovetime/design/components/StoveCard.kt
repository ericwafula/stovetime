package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeLedge
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun StoveCard(
    modifier: Modifier = Modifier,
    color: Color = StovetimeTheme.colors.surface,
    ledge: Color = StovetimeTheme.colors.border,
    bordered: Boolean = true,
    shape: RoundedCornerShape = StovetimeTheme.shapes.large,
    padding: Dp = 16.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    LedgeSurface(
        face = color,
        ledge = ledge,
        shape = shape,
        ledgeHeight = StovetimeLedge.Regular,
        border = if (bordered) StovetimeTheme.colors.border else null,
        onClick = onClick,
        modifier = modifier,
        contentAlignment = Alignment.TopStart,
    ) {
        Column(
            modifier = if (horizontalAlignment == Alignment.Start) Modifier.padding(padding) else Modifier.fillMaxWidth().padding(padding),
            horizontalAlignment = horizontalAlignment,
            content = content,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun StoveCardPreview() {
    StovetimeTheme {
        StoveCard(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            StoveText("Up next · 15 min", style = StovetimeTheme.typography.caption, color = StovetimeTheme.colors.textSecondary)
            StoveText("Jammy eggs on toast", style = StovetimeTheme.typography.heading)
        }
    }
}
