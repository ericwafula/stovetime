package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeTheme

private const val PHOTO_ASPECT_RATIO = 148f / 100f

@Composable
fun StovePhotoCard(
    title: String,
    caption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    photo: @Composable BoxScope.() -> Unit,
) {
    val colors = StovetimeTheme.colors
    val shape = StovetimeTheme.shapes.medium
    LedgeSurface(
        face = colors.surface,
        ledge = colors.border,
        shape = shape,
        ledgeHeight = 5.dp,
        border = colors.border,
        onClick = onClick,
        contentAlignment = Alignment.TopStart,
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = "$title, $caption" },
    ) {
        Column(
            modifier = Modifier.clip(shape).clearAndSetSemantics { },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(PHOTO_ASPECT_RATIO)
                        .background(colors.sunken),
                content = photo,
            )
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                StoveText(text = title, style = StovetimeTheme.typography.bodyStrong, maxLines = 2)
                StoveText(text = caption, style = StovetimeTheme.typography.caption, color = colors.textSecondary, maxLines = 1)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun StovePhotoCardPreview() {
    StovetimeTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StovePhotoCard(title = "Chicken Handi", caption = "Chicken", onClick = {}, modifier = Modifier.width(148.dp)) {}
            StovePhotoCard(title = "Beef and Mustard Pie", caption = "Beef", onClick = {}, modifier = Modifier.width(148.dp)) {}
        }
    }
}
