package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeTheme

enum class StatTone { Xp, Success, Primary }

@Composable
fun StatTile(
    value: String,
    label: String,
    tone: StatTone,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    val (background, content) =
        when (tone) {
            StatTone.Xp -> colors.xpSoft to colors.xpShade
            StatTone.Success -> colors.successSoft to colors.successShade
            StatTone.Primary -> colors.primarySoft to colors.primary
        }
    Column(
        modifier =
            modifier
                .background(background, StovetimeTheme.shapes.medium)
                .clearAndSetSemantics { contentDescription = "$label: $value" }
                .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        StoveText(text = value, style = StovetimeTheme.typography.title, color = content, textAlign = TextAlign.Center)
        StoveText(text = label, style = StovetimeTheme.typography.caption, color = colors.textSecondary, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun StatTilePreview() {
    StovetimeTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(value = "+30", label = "XP earned", tone = StatTone.Xp, modifier = Modifier.weight(1f))
            StatTile(value = "14m", label = "Cook time", tone = StatTone.Success, modifier = Modifier.weight(1f))
            StatTile(value = "3", label = "Week streak", tone = StatTone.Primary, modifier = Modifier.weight(1f))
        }
    }
}
