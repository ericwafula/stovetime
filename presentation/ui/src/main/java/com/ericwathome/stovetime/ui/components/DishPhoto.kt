package com.ericwathome.stovetime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.ui.helpers.bundledPhotoPainter

@Composable
fun BundledPhoto(
    name: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val painter = bundledPhotoPainter(name)
    if (painter == null) {
        Box(modifier.background(StovetimeTheme.colors.sunken))
    } else {
        Image(painter = painter, contentDescription = contentDescription, contentScale = ContentScale.Crop, modifier = modifier)
    }
}
