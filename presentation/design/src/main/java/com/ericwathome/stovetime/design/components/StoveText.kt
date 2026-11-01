package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun StoveText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = StovetimeTheme.typography.body,
    color: Color = StovetimeTheme.colors.textPrimary,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = color, textAlign = textAlign),
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
    )
}
