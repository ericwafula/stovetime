package com.ericwathome.stovetime.ui.launchers.quickTimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.ericwathome.stovetime.design.components.StoveBottomSheet
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveStepButton
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.components.StoveTextField
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.ui.R
import com.ericwathome.stovetime.ui.helpers.countdownText
import kotlinx.coroutines.launch

@Immutable
data class QuickTimerForm(
    val name: String = "",
    val durationSeconds: Int = 300,
)

class QuickTimerLauncher internal constructor(
    private val onLaunch: () -> Unit,
) {
    fun launch() {
        onLaunch()
    }
}

@Composable
fun rememberQuickTimerLauncher(
    form: QuickTimerForm,
    onNameChange: (String) -> Unit,
    onAdjust: (deltaSeconds: Int) -> Unit,
    onStart: () -> Unit,
): QuickTimerLauncher {
    var isVisible by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val currentOnStart by rememberUpdatedState(onStart)

    if (isVisible) {
        StoveBottomSheet(onDismiss = { isVisible = false }) {
            QuickTimerLauncherContent(
                form = form,
                onNameChange = onNameChange,
                onAdjust = onAdjust,
                onStart = {
                    keyboard?.hide()
                    isVisible = false
                    currentOnStart()
                },
            )
        }
    }

    return remember {
        QuickTimerLauncher(
            onLaunch = {
                scope.launch {
                    keyboard?.hide()
                    isVisible = true
                }
            },
        )
    }
}

@Composable
private fun QuickTimerLauncherContent(
    form: QuickTimerForm,
    onNameChange: (String) -> Unit,
    onAdjust: (deltaSeconds: Int) -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    val time = countdownText(form.durationSeconds * MILLIS_PER_SECOND)
    val timeDescription = stringResource(R.string.quick_timer_length_description, time)
    Column(
        modifier = modifier.fillMaxWidth().imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.s16),
    ) {
        StoveText(
            text = stringResource(R.string.quick_timer_title),
            style = StovetimeTheme.typography.title,
            modifier = Modifier.fillMaxWidth().semantics { heading() },
        )
        StoveTextField(
            value = form.name,
            onValueChange = onNameChange,
            placeholder = stringResource(R.string.quick_timer_name_placeholder),
            modifier = Modifier.testTag("quick_timer_name"),
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.s16)) {
            StoveStepButton(
                text = stringResource(R.string.quick_timer_minus_minute),
                contentDescription = stringResource(R.string.quick_timer_minus_minute_description),
                onClick = { onAdjust(-SECONDS_PER_MINUTE) },
            )
            StoveText(
                text = time,
                style = StovetimeTheme.typography.number,
                modifier = Modifier.semantics { contentDescription = timeDescription },
            )
            StoveStepButton(
                text = stringResource(R.string.quick_timer_plus_minute),
                contentDescription = stringResource(R.string.quick_timer_plus_minute_description),
                onClick = { onAdjust(SECONDS_PER_MINUTE) },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s16)) {
            StoveStepButton(
                text = stringResource(R.string.quick_timer_minus_seconds),
                contentDescription = stringResource(R.string.quick_timer_minus_seconds_description),
                onClick = { onAdjust(-SECONDS_STEP) },
            )
            StoveStepButton(
                text = stringResource(R.string.quick_timer_plus_seconds),
                contentDescription = stringResource(R.string.quick_timer_plus_seconds_description),
                onClick = { onAdjust(SECONDS_STEP) },
            )
        }
        StoveButton(
            text = stringResource(R.string.quick_timer_start),
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().testTag("quick_timer_start"),
        )
    }
}

private const val MILLIS_PER_SECOND = 1_000L
private const val SECONDS_PER_MINUTE = 60
private const val SECONDS_STEP = 10

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun QuickTimerLauncherContentPreview() {
    StovetimeTheme {
        QuickTimerLauncherContent(form = QuickTimerForm(), onNameChange = {}, onAdjust = {}, onStart = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun QuickTimerLauncherContentFilledPreview() {
    StovetimeTheme {
        QuickTimerLauncherContent(form = QuickTimerForm(name = "Rice", durationSeconds = 760), onNameChange = {}, onAdjust = {}, onStart = {})
    }
}
