package com.ericwathome.stovetime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import coil3.compose.AsyncImage
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Composable
fun RemotePhoto(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(StovetimeTheme.colors.sunken)) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun RemotePhotoPreview() {
    StovetimeTheme {
        RemotePhoto(url = null, contentDescription = null, modifier = Modifier.size(148.dp, 100.dp))
    }
}
