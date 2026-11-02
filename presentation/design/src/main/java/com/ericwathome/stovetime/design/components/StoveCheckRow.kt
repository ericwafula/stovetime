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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimePreviewTheme
import com.ericwathome.stovetime.design.theme.StovetimeTheme

private val CheckShape = RoundedCornerShape(6.dp)

@Composable
fun StoveCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    Box(
        modifier =
            modifier
                .size(20.dp)
                .then(
                    if (checked) {
                        Modifier.background(colors.success, CheckShape)
                    } else {
                        Modifier.border(2.dp, colors.border, CheckShape)
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            StoveIcon(icon = StoveIcons.Check, contentDescription = null, tint = colors.onAccent, size = 14.dp)
        }
    }
}

@Composable
fun StoveCheckRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StoveCheckbox(checked = checked)
        StoveText(text = text, color = if (checked) colors.textSecondary else colors.textPrimary)
    }
}

@PreviewLightDark
@Composable
private fun StoveCheckRowPreview() {
    StovetimePreviewTheme {
        Column(Modifier.padding(16.dp)) {
            StoveCheckRow(text = "2 eggs, straight from the fridge", checked = false, onCheckedChange = {})
            StoveCheckRow(text = "1 thick slice of sourdough", checked = true, onCheckedChange = {})
        }
    }
}
