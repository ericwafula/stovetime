package com.ericwathome.stovetime.ui.helpers

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val SHARE_DIRECTORY = "shared"
private const val SHARE_FILE = "cook.png"
private const val PNG_QUALITY = 100

class ImageShare internal constructor(
    internal val layer: GraphicsLayer,
    private val context: Context,
) {
    suspend fun send(caption: String) {
        val bitmap = layer.toImageBitmap().asAndroidBitmap()
        val file =
            withContext(Dispatchers.IO) {
                shareFile(context.cacheDir).also { file ->
                    file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, it) }
                }
            }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.share", file)
        val send =
            Intent(Intent.ACTION_SEND)
                .setType("image/png")
                .putExtra(Intent.EXTRA_STREAM, uri)
                .putExtra(Intent.EXTRA_TEXT, caption)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        send.clipData = ClipData.newRawUri(null, uri)
        context.startActivity(Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

internal fun shareFile(cacheDir: File): File = File(cacheDir, SHARE_DIRECTORY).apply { mkdirs() }.resolve(SHARE_FILE)

@Composable
fun rememberImageShare(): ImageShare {
    val layer = rememberGraphicsLayer()
    val context = LocalContext.current
    return remember(layer, context) { ImageShare(layer, context) }
}

@Composable
fun ShareImageSource(
    share: ImageShare,
    content: @Composable () -> Unit,
) {
    Box(
        Modifier
            .layout { measurable, _ ->
                val placeable = measurable.measure(Constraints())
                layout(0, 0) { placeable.place(0, 0) }
            }.clearAndSetSemantics {},
    ) {
        Box(
            Modifier
                .wrapContentSize(align = Alignment.TopStart, unbounded = true)
                .drawWithContent { share.layer.record { this@drawWithContent.drawContent() } },
        ) {
            content()
        }
    }
}
