package com.ericwathome.stovetime.ui.launchers.confirm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.ericwathome.stovetime.design.components.StoveBottomSheet
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveButtonStyle
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme

@Immutable
data class ConfirmRequest(
    val title: String,
    val message: String,
    val confirmLabel: String,
    val dismissLabel: String,
    val destructive: Boolean = false,
    val tag: String = "",
)

class ConfirmLauncher internal constructor(
    private val onLaunch: (ConfirmRequest) -> Unit,
) {
    fun launch(request: ConfirmRequest) {
        onLaunch(request)
    }
}

@Composable
fun rememberConfirmLauncher(
    onConfirm: (ConfirmRequest) -> Unit,
    onSecondary: (ConfirmRequest) -> Unit = {},
    onDismiss: (ConfirmRequest) -> Unit = {},
): ConfirmLauncher {
    var request by remember { mutableStateOf<ConfirmRequest?>(null) }
    val currentOnConfirm by rememberUpdatedState(onConfirm)
    val currentOnSecondary by rememberUpdatedState(onSecondary)
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    request?.let { shown ->
        StoveBottomSheet(
            onDismiss = {
                request = null
                currentOnDismiss(shown)
            },
        ) {
            ConfirmLauncherContent(
                request = shown,
                onConfirm = {
                    request = null
                    currentOnConfirm(shown)
                },
                onSecondary = {
                    request = null
                    currentOnSecondary(shown)
                },
            )
        }
    }

    return remember { ConfirmLauncher(onLaunch = { request = it }) }
}

@Composable
private fun ConfirmLauncherContent(
    request: ConfirmRequest,
    onConfirm: () -> Unit,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    Column(modifier) {
        StoveText(text = request.title, style = StovetimeTheme.typography.title, modifier = Modifier.semantics { heading() })
        Spacer(Modifier.height(spacing.s8))
        StoveText(text = request.message, color = StovetimeTheme.colors.textSecondary)
        Spacer(Modifier.height(spacing.s24))
        StoveButton(
            text = request.confirmLabel,
            onClick = onConfirm,
            style = if (request.destructive) StoveButtonStyle.Primary else StoveButtonStyle.Success,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(spacing.s12))
        StoveButton(
            text = request.dismissLabel,
            onClick = onSecondary,
            style = StoveButtonStyle.Secondary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun ConfirmLauncherContentPreview() {
    StovetimeTheme {
        ConfirmLauncherContent(
            request =
                ConfirmRequest(
                    title = "Leave this cook?",
                    message = "Your progress on this recipe won't be saved.",
                    confirmLabel = "Leave",
                    dismissLabel = "Keep cooking",
                    destructive = true,
                ),
            onConfirm = {},
            onSecondary = {},
        )
    }
}
