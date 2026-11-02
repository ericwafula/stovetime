package com.ericwathome.stovetime.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ericwathome.stovetime.design.theme.StovetimeTheme

private val SheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoveBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = SheetShape,
        containerColor = StovetimeTheme.colors.canvas,
        scrimColor = StovetimeTheme.colors.textPrimary.copy(alpha = 0.35f),
        dragHandle = { SheetHandle() },
        contentWindowInsets = { WindowInsets(0) },
        modifier = modifier,
    ) {
        StoveSheetBody(content = content)
    }
}

@Composable
private fun SheetHandle() {
    Box(
        Modifier
            .padding(top = 12.dp, bottom = 8.dp)
            .size(width = 40.dp, height = 5.dp)
            .background(StovetimeTheme.colors.border, StovetimeTheme.shapes.full),
    )
}

@Composable
private fun StoveSheetBody(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .semantics { testTagsAsResourceId = true }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp, bottom = 24.dp),
        content = content,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun StoveSheetBodyPreview() {
    StovetimeTheme {
        Column(Modifier.background(Color.Transparent), horizontalAlignment = Alignment.CenterHorizontally) {
            SheetHandle()
            StoveSheetBody {
                StoveText(text = "Quick timer", style = StovetimeTheme.typography.title)
                Box(Modifier.height(16.dp))
                StoveButton(text = "Start timer", onClick = {}, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
