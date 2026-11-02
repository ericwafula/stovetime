package com.ericwathome.stovetime.ui.helpers

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@SuppressLint("DiscouragedApi")
@Composable
fun bundledPhotoPainter(name: String?): Painter? {
    val context = LocalContext.current
    val resource =
        remember(name) {
            name?.let { context.resources.getIdentifier(it, "drawable", context.packageName) } ?: 0
        }
    return if (resource != 0) painterResource(resource) else null
}
