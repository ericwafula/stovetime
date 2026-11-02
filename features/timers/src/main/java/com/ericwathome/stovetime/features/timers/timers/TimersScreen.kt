package com.ericwathome.stovetime.features.timers.timers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.timers.RunningTimer
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.design.components.Pill
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveButtonStyle
import com.ericwathome.stovetime.design.components.StoveCard
import com.ericwathome.stovetime.design.components.StoveIconButton
import com.ericwathome.stovetime.design.components.StoveIcons
import com.ericwathome.stovetime.design.components.StoveNavBarClearance
import com.ericwathome.stovetime.design.components.StoveProgressBar
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.timers.R
import com.ericwathome.stovetime.ui.helpers.CollectOneTimeEvent
import com.ericwathome.stovetime.ui.helpers.countdownText
import com.ericwathome.stovetime.ui.helpers.hasNotificationPermission
import com.ericwathome.stovetime.ui.helpers.openExactAlarmSettings
import com.ericwathome.stovetime.ui.helpers.rememberNotificationPermissionRequest
import com.ericwathome.stovetime.ui.helpers.rememberTickingNow
import com.ericwathome.stovetime.ui.launchers.confirm.ConfirmRequest
import com.ericwathome.stovetime.ui.launchers.confirm.rememberConfirmLauncher
import com.ericwathome.stovetime.ui.launchers.quickTimer.rememberQuickTimerLauncher
import org.koin.compose.viewmodel.koinViewModel

private const val NOTIFICATIONS = "notifications"
private const val LATE = "late"

@Composable
fun TimersRoute(
    onOpenCookStep: (kind: CookKind, dishId: String, startedAt: Long, step: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    TimersScreen(onOpenCookStep = onOpenCookStep, modifier = modifier)
}

@Composable
internal fun TimersScreen(
    onOpenCookStep: (kind: CookKind, dishId: String, startedAt: Long, step: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var notificationsAllowed by remember { mutableStateOf(context.hasNotificationPermission()) }
    LifecycleResumeEffect(Unit) {
        notificationsAllowed = context.hasNotificationPermission()
        onPauseOrDispose {}
    }
    val permissionRequest =
        rememberNotificationPermissionRequest { granted ->
            notificationsAllowed = granted
            viewModel.onAction(TimersAction.OnNotificationPermissionResult)
        }
    val confirmLauncher =
        rememberConfirmLauncher(
            onConfirm = { request ->
                when (request.tag) {
                    NOTIFICATIONS -> viewModel.onAction(TimersAction.OnNotificationsExplained)
                    LATE -> context.openExactAlarmSettings()
                }
            },
            onSecondary = { request ->
                if (request.tag == NOTIFICATIONS) viewModel.onAction(TimersAction.OnNotificationPermissionResult)
            },
            onDismiss = { request ->
                if (request.tag == NOTIFICATIONS) viewModel.onAction(TimersAction.OnNotificationPermissionResult)
            },
        )
    val notificationsRequest =
        timersRequest(
            NOTIFICATIONS,
            R.string.timers_notifications_title,
            R.string.timers_notifications_message,
            R.string.timers_notifications_confirm,
            R.string.timers_notifications_dismiss,
        )
    val lateRequest = timersRequest(LATE, R.string.timers_late_title, R.string.timers_late_message, R.string.timers_late_confirm, R.string.timers_late_dismiss)
    val defaultNameFormat = stringResource(R.string.timers_default_name)
    val quickTimerLauncher =
        rememberQuickTimerLauncher(
            form = state.quickTimer,
            onNameChange = { viewModel.onAction(TimersAction.OnQuickTimerNameChange(it)) },
            onAdjust = { viewModel.onAction(TimersAction.OnQuickTimerAdjust(it)) },
            onStart = {
                viewModel.onAction(
                    TimersAction.OnClickStartQuickTimer(defaultNameFormat = defaultNameFormat, notificationsAllowed = notificationsAllowed),
                )
            },
        )

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            TimersEvent.OpenQuickTimer -> quickTimerLauncher.launch()
            TimersEvent.ExplainNotifications -> confirmLauncher.launch(notificationsRequest)
            TimersEvent.RequestNotificationPermission -> permissionRequest.launch()
            TimersEvent.ExplainLateAlarms -> confirmLauncher.launch(lateRequest)
            is TimersEvent.OpenCookStep -> onOpenCookStep(event.kind, event.dishId, event.startedAt, event.step)
        }
    }

    TimersContent(state = state, notificationsAllowed = notificationsAllowed, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun timersRequest(
    tag: String,
    title: Int,
    message: Int,
    confirm: Int,
    dismiss: Int,
) = ConfirmRequest(
    title = stringResource(title),
    message = stringResource(message),
    confirmLabel = stringResource(confirm),
    dismissLabel = stringResource(dismiss),
    tag = tag,
)

@Composable
private fun TimersContent(
    state: TimersState,
    notificationsAllowed: Boolean,
    onAction: (TimersAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    val nowMillis = rememberTickingNow(ticking = state.isTicking, initialMillis = state.nowMillis)
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(StovetimeTheme.colors.canvas)
                .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        if (state.isEmpty) {
            TimersHeader(count = 0, modifier = Modifier.padding(start = spacing.s24, end = spacing.s24, top = spacing.s24))
            EmptyTimers(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = spacing.s24, end = spacing.s24, top = spacing.s24, bottom = spacing.s20),
                verticalArrangement = Arrangement.spacedBy(spacing.s20),
            ) {
                item(key = "header") { TimersHeader(count = state.timers.size) }
                itemsIndexed(state.timers, key = { _, timer -> timer.id }) { index, timer ->
                    TimerCard(
                        timer = timer,
                        nowMillis = nowMillis,
                        onAction = onAction,
                        modifier = Modifier.testTag("timer_card_$index"),
                    )
                }
                if (!notificationsAllowed) {
                    item(key = "notifications_off") {
                        StoveText(
                            text = stringResource(R.string.timers_notifications_off),
                            style = StovetimeTheme.typography.caption,
                            color = StovetimeTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
        StoveButton(
            text = stringResource(R.string.timers_new_quick_timer),
            onClick = { onAction(TimersAction.OnClickNewQuickTimer) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(start = spacing.s24, end = spacing.s24, bottom = StoveNavBarClearance)
                    .testTag("timers_new_quick_timer"),
        )
    }
}

@Composable
private fun TimersHeader(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        StoveText(
            text = stringResource(R.string.timers_title),
            style = StovetimeTheme.typography.title,
            modifier = Modifier.semantics { heading() },
        )
        if (count > 0) {
            StoveText(
                text = pluralStringResource(R.plurals.timers_running, count, count),
                style = StovetimeTheme.typography.caption,
                color = StovetimeTheme.colors.textSecondary,
            )
        }
    }
}

@Composable
private fun EmptyTimers(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(StovetimeTheme.spacing.s24), contentAlignment = Alignment.Center) {
        StoveText(
            text = stringResource(R.string.timers_empty),
            style = StovetimeTheme.typography.heading,
            color = StovetimeTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TimerCard(
    timer: RunningTimer,
    nowMillis: Long,
    onAction: (TimersAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = StovetimeTheme.colors
    val spacing = StovetimeTheme.spacing
    val remaining = timer.remainingMillis(nowMillis)
    val time = countdownText(remaining)
    val description =
        when {
            timer.ringing -> stringResource(R.string.timers_card_ringing, timer.name)
            timer.isPaused -> stringResource(R.string.timers_card_paused, timer.name, time)
            else -> stringResource(R.string.timers_card_running, timer.name, time)
        }
    val detail = timer.detail ?: if (timer.target == TimerTarget.Quick) stringResource(R.string.timers_quick_timer) else null
    val progress = if (timer.ringing || timer.durationMillis <= 0) 1f else 1f - remaining.toFloat() / timer.durationMillis
    val opensCook = timer.target is TimerTarget.CookStep
    StoveCard(
        modifier = modifier.fillMaxWidth(),
        onClick = if (opensCook) ({ onAction(TimersAction.OnClickTimer(timer.id)) }) else null,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.s8)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.s8)) {
                StoveText(
                    text = timer.name,
                    style = StovetimeTheme.typography.bodyStrong,
                    modifier =
                        Modifier.weight(1f).clearAndSetSemantics {
                            contentDescription = description
                            heading()
                        },
                )
                if (timer.isPaused) {
                    Pill(text = stringResource(R.string.timers_paused), color = colors.textSecondary, modifier = Modifier.clearAndSetSemantics {})
                }
                StoveIconButton(
                    icon = StoveIcons.Close,
                    contentDescription = stringResource(R.string.timers_cancel_description, timer.name),
                    onClick = { onAction(TimersAction.OnClickCancel(timer.id)) },
                )
            }
            if (timer.ringing) {
                StoveText(
                    text = stringResource(R.string.timers_times_up),
                    style = StovetimeTheme.typography.title,
                    color = colors.primary,
                    modifier = Modifier.clearAndSetSemantics {},
                )
            } else {
                StoveText(
                    text = time,
                    style = StovetimeTheme.typography.number,
                    color = if (timer.isPaused) colors.textSecondary else colors.textPrimary,
                    modifier = Modifier.clearAndSetSemantics {},
                )
            }
            detail?.let {
                StoveText(text = it, style = StovetimeTheme.typography.caption, color = colors.textSecondary)
            }
            StoveProgressBar(progress = progress)
            TimerActions(timer = timer, onAction = onAction)
        }
    }
}

@Composable
private fun TimerActions(
    timer: RunningTimer,
    onAction: (TimersAction) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(StovetimeTheme.spacing.s12), modifier = Modifier.fillMaxWidth()) {
        val (label, action) =
            when {
                timer.ringing -> R.string.timers_stop to TimersAction.OnClickStop(timer.id)
                timer.isPaused -> R.string.timers_resume to TimersAction.OnClickResume(timer.id)
                else -> R.string.timers_pause to TimersAction.OnClickPause(timer.id)
            }
        StoveButton(
            text = stringResource(label),
            onClick = { onAction(action) },
            style = StoveButtonStyle.Secondary,
            modifier = Modifier.weight(1f),
        )
        StoveButton(
            text = stringResource(R.string.timers_plus_minute),
            onClick = { onAction(TimersAction.OnClickAddMinute(timer.id)) },
            style = StoveButtonStyle.Secondary,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun timersForPreview(): List<RunningTimer> =
    listOf(
        RunningTimer(
            id = "eggs",
            name = "Jammy eggs · Step 2",
            detail = "for a jammy, set-at-the-edges yolk",
            durationMillis = 390_000,
            endsAtMillis = 252_000,
            pausedRemainingMillis = null,
            target = TimerTarget.CookStep(CookKind.Lesson, "jammy-eggs-on-toast", 0, 1),
            ringing = false,
        ),
        RunningTimer(
            id = "rice",
            name = "Rice",
            detail = null,
            durationMillis = 1_500_000,
            endsAtMillis = null,
            pausedRemainingMillis = 760_000,
            target = TimerTarget.Quick,
            ringing = false,
        ),
    )

@Preview
@Composable
private fun TimersContentPreview() {
    StovetimeTheme {
        TimersContent(state = TimersState(timers = timersForPreview()), notificationsAllowed = true, onAction = {})
    }
}

@Preview
@Composable
private fun TimersContentRingingPreview() {
    StovetimeTheme {
        val ringing = timersForPreview().first().copy(endsAtMillis = 0, ringing = true)
        TimersContent(state = TimersState(timers = listOf(ringing)), notificationsAllowed = false, onAction = {})
    }
}

@Preview
@Composable
private fun TimersContentEmptyPreview() {
    StovetimeTheme {
        TimersContent(state = TimersState(), notificationsAllowed = true, onAction = {})
    }
}
