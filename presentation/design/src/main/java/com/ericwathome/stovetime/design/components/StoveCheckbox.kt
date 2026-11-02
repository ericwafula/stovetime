package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun StoveCheckRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    val shape = StovetimeTheme.shapes.small
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(24.dp)
                    .background(if (checked) colors.success else colors.surface, shape)
                    .border(2.dp, if (checked) colors.success else colors.border, shape),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) StoveIcon(StoveIcons.Check, contentDescription = null, tint = colors.onAccent, size = 16.dp)
        }
        StoveText(
            text = text,
            color = if (checked) colors.textSecondary else colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun StoveCheckRowPreview() {
    StovetimeTheme {
        Column(Modifier.padding(16.dp)) {
            StoveCheckRow(text = "2 eggs, straight from the fridge", checked = true, onCheckedChange = {})
            StoveCheckRow(text = "1 thick slice of sourdough", checked = false, onCheckedChange = {})
        }
    }
}
