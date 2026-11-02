package com.ericwathome.stovetime.features.timers.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.timers.RunningTimer
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveButtonStyle
import com.ericwathome.stovetime.design.components.StoveIconDisc
import com.ericwathome.stovetime.design.components.StoveIcons
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.timers.R
import com.ericwathome.stovetime.ui.helpers.CollectOneTimeEvent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AlarmRoute(
    timerId: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlarmScreen(timerId = timerId, onDismiss = onDismiss, modifier = modifier)
}

@Composable
internal fun AlarmScreen(
    timerId: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(timerId) { viewModel.onAction(AlarmAction.OnShowTimer(timerId)) }

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            AlarmEvent.Dismiss -> onDismiss()
        }
    }

    AlarmContent(state = state, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun AlarmContent(
    state: AlarmState,
    onAction: (AlarmAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(StovetimeTheme.colors.primary)
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = spacing.s24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        state.timer?.let { AlarmMessage(timer = it) }
        Spacer(Modifier.weight(1.4f))
        AlarmActions(onAction = onAction)
    }
}

@Composable
private fun AlarmMessage(timer: RunningTimer) {
    val colors = StovetimeTheme.colors
    val spacing = StovetimeTheme.spacing
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(spacing.s12)) {
        StoveIconDisc(icon = StoveIcons.Timer)
        Spacer(Modifier.height(spacing.s16))
        StoveText(
            text = stringResource(R.string.alarm_times_up).uppercase(),
            style = StovetimeTheme.typography.label,
            color = colors.onAccent,
        )
        StoveText(
            text = timer.name,
            style = StovetimeTheme.typography.display,
            color = colors.onAccent,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().semantics { heading() },
        )
        timer.detail?.let { detail ->
            StoveText(
                text = stringResource(R.string.alarm_detail, detail),
                color = colors.onAccent,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AlarmActions(onAction: (AlarmAction) -> Unit) {
    val spacing = StovetimeTheme.spacing
    val plusMinuteDescription = stringResource(R.string.alarm_plus_minute_description)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.s8),
        modifier = Modifier.fillMaxWidth().padding(bottom = spacing.s24),
    ) {
        StoveButton(
            text = stringResource(R.string.alarm_stop),
            onClick = { onAction(AlarmAction.OnClickStop) },
            style = StoveButtonStyle.Secondary,
            modifier = Modifier.fillMaxWidth().testTag("alarm_stop"),
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .heightIn(min = 48.dp)
                    .clickable(role = Role.Button, onClickLabel = plusMinuteDescription) { onAction(AlarmAction.OnClickPlusMinute) }
                    .padding(horizontal = spacing.s16)
                    .testTag("alarm_plus_minute"),
        ) {
            StoveText(
                text = stringResource(R.string.alarm_plus_minute),
                style = StovetimeTheme.typography.button,
                color = StovetimeTheme.colors.onAccent,
            )
        }
    }
}

private fun alarmTimerForPreview(detail: String?) =
    RunningTimer(
        id = "t1",
        name = "Jammy eggs · Step 2",
        detail = detail,
        durationMillis = 390_000,
        endsAtMillis = 0,
        pausedRemainingMillis = null,
        target = TimerTarget.CookStep(CookKind.Lesson, "jammy-eggs-on-toast", 0, 1),
        ringing = true,
    )

@Preview
@Composable
private fun AlarmContentPreview() {
    StovetimeTheme {
        AlarmContent(state = AlarmState(timer = alarmTimerForPreview("for a jammy, set-at-the-edges yolk")), onAction = {})
    }
}

@Preview
@Composable
private fun AlarmContentQuickTimerPreview() {
    StovetimeTheme {
        AlarmContent(state = AlarmState(timer = alarmTimerForPreview(null).copy(name = "Rice", target = TimerTarget.Quick)), onAction = {})
    }
}
