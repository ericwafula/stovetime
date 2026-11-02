package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun StoveToast(
    text: String,
    modifier: Modifier = Modifier,
) {
    StoveText(
        text = text,
        style = StovetimeTheme.typography.label,
        color = StovetimeTheme.colors.onAccent,
        modifier =
            modifier
                .semantics { liveRegion = LiveRegionMode.Polite }
                .background(StovetimeTheme.colors.textPrimary, StovetimeTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun StoveToastPreview() {
    StovetimeTheme {
        StoveToast(text = "Finish the previous lesson first", modifier = Modifier.padding(16.dp))
    }
}
