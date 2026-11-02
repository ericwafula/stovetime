package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeLedge
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun StoveChoice(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    LedgeSurface(
        face = if (selected) colors.successSoft else colors.surface,
        ledge = if (selected) colors.success else colors.border,
        shape = StovetimeTheme.shapes.medium,
        ledgeHeight = StovetimeLedge.Small,
        border = if (selected) colors.success else colors.border,
        modifier = modifier.heightIn(min = 52.dp).selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
    ) {
        StoveText(
            text = text,
            style = StovetimeTheme.typography.label,
            color = if (selected) colors.successShade else colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun StoveChoicePreview() {
    StovetimeTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StoveChoice(text = "Overdone", selected = false, onClick = {}, modifier = Modifier.weight(1f))
            StoveChoice(text = "Pretty good", selected = true, onClick = {}, modifier = Modifier.weight(1f))
            StoveChoice(text = "Nailed it", selected = false, onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}
