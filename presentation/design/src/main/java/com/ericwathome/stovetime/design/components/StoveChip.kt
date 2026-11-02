package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeTheme

private val ChipShape = RoundedCornerShape(12.dp)

@Composable
fun StoveChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    Box(
        modifier =
            modifier
                .selectable(selected = selected, role = Role.Tab, onClick = onClick)
                .padding(vertical = 7.dp),
    ) {
        StoveText(
            text = text,
            style = StovetimeTheme.typography.label,
            color = if (selected) colors.onAccent else colors.textPrimary,
            maxLines = 1,
            modifier =
                Modifier
                    .background(if (selected) colors.primary else colors.surface, ChipShape)
                    .then(if (selected) Modifier else Modifier.border(2.dp, colors.border, ChipShape))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun StoveChipPreview() {
    StovetimeTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StoveChip(text = "All", selected = true, onClick = {})
            StoveChip(text = "Chicken", selected = false, onClick = {})
            StoveChip(text = "Beef", selected = false, onClick = {})
        }
    }
}
