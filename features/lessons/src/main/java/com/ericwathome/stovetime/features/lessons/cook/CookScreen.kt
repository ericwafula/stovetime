package com.ericwathome.stovetime.features.lessons.cook

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.data.model.cook.CookPlan
import com.ericwathome.stovetime.core.data.model.cook.CookStep
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.timers.RunningTimer
import com.ericwathome.stovetime.core.data.model.timers.TimerTarget
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveButtonStyle
import com.ericwathome.stovetime.design.components.StoveCard
import com.ericwathome.stovetime.design.components.StoveIcon
import com.ericwathome.stovetime.design.components.StoveIcons
import com.ericwathome.stovetime.design.components.StoveProgressBar
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.components.TimerChip
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.lessons.R
import com.ericwathome.stovetime.ui.helpers.CollectOneTimeEvent
import com.ericwathome.stovetime.ui.helpers.KeepScreenOn
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.countdownText
import com.ericwathome.stovetime.ui.helpers.hasNotificationPermission
import com.ericwathome.stovetime.ui.helpers.openExactAlarmSettings
import com.ericwathome.stovetime.ui.helpers.openFullScreenAlarmSettings
import com.ericwathome.stovetime.ui.helpers.rememberTickingNow
import com.ericwathome.stovetime.ui.helpers.rememberNotificationPermissionRequest
import com.ericwathome.stovetime.ui.launchers.confirm.ConfirmRequest
import com.ericwathome.stovetime.ui.launchers.confirm.rememberConfirmLauncher
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val LEAVE = "leave"
private const val KEEP_ON_LEAVE = "keep_on_leave"
private const val KEEP_ON_FINISH = "keep_on_finish"
private const val NOTIFICATIONS = "notifications"
private const val LATE = "late"
private const val LOCK_SCREEN = "lock_screen"

@Composable
fun CookRoute(
    kind: CookKind,
    dishId: String,
    startedAt: Long,
    step: Int,
    onClose: () -> Unit,
    onFinish: (kind: CookKind, dishId: String, startedAt: Long, finishedAt: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    CookScreen(
        onClose = onClose,
        onFinish = onFinish,
        modifier = modifier,
        viewModel =
            koinViewModel(key = "cook_${kind}_${dishId}_$startedAt") {
                parametersOf(CookArgs(kind = kind, dishId = dishId, startedAt = startedAt, step = step))
            },
    )
}

@Composable
internal fun CookScreen(
    onClose: () -> Unit,
    onFinish: (kind: CookKind, dishId: String, startedAt: Long, finishedAt: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CookViewModel = koinViewModel(),
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
            viewModel.onAction(CookAction.OnNotificationPermissionResult)
        }
    val confirmLauncher =
        rememberConfirmLauncher(
            onConfirm = { request ->
                when (request.tag) {
                    LEAVE -> viewModel.onAction(CookAction.OnConfirmLeave(keepTimers = null))
                    KEEP_ON_LEAVE -> viewModel.onAction(CookAction.OnConfirmLeave(keepTimers = true))
                    KEEP_ON_FINISH -> viewModel.onAction(CookAction.OnConfirmFinish(keepTimers = true))
                    NOTIFICATIONS -> viewModel.onAction(CookAction.OnNotificationsExplained)
                    LATE -> context.openExactAlarmSettings()
                    LOCK_SCREEN -> context.openFullScreenAlarmSettings()
                }
            },
            onSecondary = { request ->
                when (request.tag) {
                    KEEP_ON_LEAVE -> viewModel.onAction(CookAction.OnConfirmLeave(keepTimers = false))
                    KEEP_ON_FINISH -> viewModel.onAction(CookAction.OnConfirmFinish(keepTimers = false))
                    NOTIFICATIONS -> viewModel.onAction(CookAction.OnNotificationPermissionResult)
                }
            },
            onDismiss = { request ->
                if (request.tag == NOTIFICATIONS) viewModel.onAction(CookAction.OnNotificationPermissionResult)
            },
        )
    val leaveRequest = cookRequest(LEAVE, R.string.cook_leave_title, R.string.cook_leave_message, R.string.cook_leave_confirm, R.string.cook_leave_dismiss, true)
    val keepOnLeaveRequest = cookRequest(KEEP_ON_LEAVE, R.string.cook_keep_title, R.string.cook_keep_message, R.string.cook_keep_confirm, R.string.cook_keep_cancel)
    val keepOnFinishRequest = keepOnLeaveRequest.copy(tag = KEEP_ON_FINISH)
    val notificationsRequest =
        cookRequest(
            NOTIFICATIONS,
            R.string.cook_notifications_title,
            R.string.cook_notifications_message,
            R.string.cook_notifications_confirm,
            R.string.cook_notifications_dismiss,
        )
    val lateRequest = cookRequest(LATE, R.string.cook_late_title, R.string.cook_late_message, R.string.cook_late_confirm, R.string.cook_late_dismiss)
    val lockScreenRequest =
        cookRequest(LOCK_SCREEN, R.string.cook_lock_title, R.string.cook_lock_message, R.string.cook_lock_confirm, R.string.cook_lock_dismiss)

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            CookEvent.AskToLeave -> confirmLauncher.launch(leaveRequest)
            CookEvent.AskToKeepTimersOnLeave -> confirmLauncher.launch(keepOnLeaveRequest)
            CookEvent.AskToKeepTimersOnFinish -> confirmLauncher.launch(keepOnFinishRequest)
            CookEvent.ExplainNotifications -> confirmLauncher.launch(notificationsRequest)
            CookEvent.RequestNotificationPermission -> permissionRequest.launch()
            CookEvent.ExplainLateAlarms -> confirmLauncher.launch(lateRequest)
            CookEvent.ExplainLockScreenAlarms -> confirmLauncher.launch(lockScreenRequest)
            CookEvent.Close -> onClose()
            is CookEvent.Finish -> onFinish(event.kind, event.dishId, event.startedAt, event.finishedAt)
        }
    }

    BackHandler { viewModel.onAction(CookAction.OnClickBack) }
    KeepScreenOn(enabled = state.keepScreenOn)

    CookContent(state = state, notificationsAllowed = notificationsAllowed, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun cookRequest(
    tag: String,
    title: Int,
    message: Int,
    confirm: Int,
    dismiss: Int,
    destructive: Boolean = false,
) = ConfirmRequest(
    title = stringResource(title),
    message = stringResource(message),
    confirmLabel = stringResource(confirm),
    dismissLabel = stringResource(dismiss),
    destructive = destructive,
    tag = tag,
)

@Composable
private fun CookContent(
    state: CookState,
    notificationsAllowed: Boolean,
    onAction: (CookAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(StovetimeTheme.colors.canvas)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        CookTopBar(state = state, onClose = { onAction(CookAction.OnClickClose) })
        when (val plan = state.plan) {
            ViewState.Initial, ViewState.Loading -> Spacer(Modifier.weight(1f))
            is ViewState.Error -> CookError(onRetry = { onAction(CookAction.OnClickRetry) }, modifier = Modifier.weight(1f))
            is ViewState.Success -> CookStepBody(plan = plan.data, state = state, notificationsAllowed = notificationsAllowed, onAction = onAction)
        }
    }
}

@Composable
private fun CookTopBar(
    state: CookState,
    onClose: () -> Unit,
) {
    val spacing = StovetimeTheme.spacing
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.s12, vertical = spacing.s8),
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onClose)
                .testTag("cook_close"),
            contentAlignment = Alignment.Center,
        ) {
            StoveIcon(StoveIcons.Close, contentDescription = stringResource(R.string.intro_close), tint = StovetimeTheme.colors.textSecondary)
        }
        StoveProgressBar(progress = state.progress, modifier = Modifier.weight(1f).padding(horizontal = spacing.s8))
        if (state.steps.isNotEmpty()) {
            StoveText(
                text = stringResource(R.string.cook_step_count, state.step + 1, state.steps.size),
                style = StovetimeTheme.typography.label,
                color = StovetimeTheme.colors.textSecondary,
                modifier = Modifier.padding(end = spacing.s12),
            )
        }
    }
}

@Composable
private fun ColumnScope.CookStepBody(
    plan: CookPlan,
    state: CookState,
    notificationsAllowed: Boolean,
    onAction: (CookAction) -> Unit,
) {
    val step = state.currentStep ?: return
    val nowMillis = rememberTickingNow(ticking = state.timers.any { !it.isPaused && !it.ringing }, initialMillis = state.nowMillis)
    val spacing = StovetimeTheme.spacing
    val colors = StovetimeTheme.colors
    val timerName = stringResource(R.string.cook_timer_name, plan.name, state.step + 1)
    Column(
        Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.s24, vertical = spacing.s16),
    ) {
        StoveText(
            text = stringResource(R.string.cook_step, state.step + 1).uppercase(),
            style = StovetimeTheme.typography.label,
            color = colors.primaryContent,
        )
        Spacer(Modifier.height(spacing.s12))
        StoveText(
            text = step.text,
            style = StovetimeTheme.typography.title,
            modifier = Modifier.semantics { heading() }.testTag("cook_step_text"),
        )
        step.detail?.let {
            Spacer(Modifier.height(spacing.s12))
            StoveText(text = it, color = colors.textSecondary)
        }
        step.timerSeconds?.let { seconds ->
            Spacer(Modifier.height(spacing.s24))
            TimerCard(
                totalMillis = seconds * 1_000L,
                doneness = step.doneness,
                timer = state.currentTimer,
                nowMillis = nowMillis,
                timerName = timerName,
                notificationsAllowed = notificationsAllowed,
                onAction = onAction,
            )
        }
        step.tip?.let {
            Spacer(Modifier.height(spacing.s20))
            TipCard(tip = it)
        }
    }
    Column(Modifier.padding(horizontal = spacing.s24, vertical = spacing.s16)) {
        if (state.otherTimers.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.s8),
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(bottom = spacing.s12),
            ) {
                state.otherTimers.forEach { timer ->
                    val timerStep = state.stepOf(timer) ?: return@forEach
                    TimerChip(
                        text =
                            stringResource(
                                R.string.cook_timer_chip,
                                timerStep + 1,
                                if (timer.ringing) stringResource(R.string.cook_times_up) else countdownText(timer.remainingMillis(nowMillis)),
                            ),
                        onClick = { onAction(CookAction.OnClickTimerChip(timerStep)) },
                    )
                }
            }
        }
        StoveButton(
            text = stringResource(if (state.isLastStep) R.string.cook_finish else R.string.cook_next),
            onClick = { onAction(CookAction.OnClickNext) },
            style = if (state.isLastStep) StoveButtonStyle.Success else StoveButtonStyle.Primary,
            modifier = Modifier.fillMaxWidth().testTag("cook_next"),
        )
    }
}

@Composable
private fun TimerCard(
    totalMillis: Long,
    doneness: String?,
    timer: RunningTimer?,
    nowMillis: Long,
    timerName: String,
    notificationsAllowed: Boolean,
    onAction: (CookAction) -> Unit,
) {
    val colors = StovetimeTheme.colors
    val spacing = StovetimeTheme.spacing
    val remaining = timer?.remainingMillis(nowMillis) ?: totalMillis
    val time = countdownText(remaining)
    val description = stringResource(R.string.cook_timer_description, timerName, time)
    StoveCard(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, padding = 20.dp) {
        if (timer?.ringing == true) {
            StoveText(text = stringResource(R.string.cook_times_up), style = StovetimeTheme.typography.title, color = colors.primaryContent)
        } else {
            StoveText(
                text = time,
                style = StovetimeTheme.typography.number,
                modifier = Modifier.semantics { contentDescription = description }.testTag("cook_timer_time"),
            )
        }
        doneness?.let {
            Spacer(Modifier.height(spacing.s4))
            StoveText(text = it, style = StovetimeTheme.typography.caption, color = colors.textSecondary, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(spacing.s16))
        when {
            timer == null -> {
                TimerChip(
                    text = stringResource(R.string.cook_start_timer),
                    onClick = { onAction(CookAction.OnClickStartTimer(name = timerName, notificationsAllowed = notificationsAllowed)) },
                    modifier = Modifier.testTag("cook_start_timer"),
                )
            }

            timer.ringing -> {
                StoveButton(text = stringResource(R.string.cook_stop), onClick = { onAction(CookAction.OnClickStopTimer(timer.id)) })
            }

            else -> {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.s12), modifier = Modifier.fillMaxWidth()) {
                    StoveButton(
                        text = stringResource(if (timer.isPaused) R.string.cook_resume else R.string.cook_pause),
                        onClick = {
                            onAction(if (timer.isPaused) CookAction.OnClickResumeTimer(timer.id) else CookAction.OnClickPauseTimer(timer.id))
                        },
                        style = StoveButtonStyle.Secondary,
                        modifier = Modifier.weight(1f).testTag("cook_pause_timer"),
                    )
                    StoveButton(
                        text = stringResource(R.string.cook_reset),
                        onClick = { onAction(CookAction.OnClickResetTimer(timer.id)) },
                        style = StoveButtonStyle.Secondary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        if (timer != null && !notificationsAllowed) {
            Spacer(Modifier.height(spacing.s12))
            StoveText(
                text = stringResource(R.string.cook_notifications_off),
                style = StovetimeTheme.typography.caption,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TipCard(tip: String) {
    val colors = StovetimeTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.xpSoft, StovetimeTheme.shapes.medium)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StoveIcon(StoveIcons.Star, contentDescription = null, tint = colors.xpContent, size = 20.dp)
        StoveText(text = tip, style = StovetimeTheme.typography.caption)
    }
}

@Composable
private fun CookError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth().padding(StovetimeTheme.spacing.s24),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StoveText(text = stringResource(R.string.cook_error), style = StovetimeTheme.typography.heading, textAlign = TextAlign.Center)
        Spacer(Modifier.height(StovetimeTheme.spacing.s16))
        StoveButton(text = stringResource(R.string.path_retry), onClick = onRetry, style = StoveButtonStyle.Secondary)
    }
}

private fun planPreviewData() =
    CookPlan(
        kind = CookKind.Lesson,
        dishId = "jammy-eggs-on-toast",
        name = "Jammy eggs on toast",
        photo = null,
        baseXp = 30,
        steps =
            listOf(
                CookStep("Bring a small pan of water to the boil", null, null, null, null),
                CookStep(
                    text = "Lower the eggs into the boiling water with a spoon",
                    detail = "Keep it at a steady boil, not a rolling one, so the shells don't crack against the pan.",
                    timerSeconds = 390,
                    doneness = "for a jammy, set-at-the-edges yolk",
                    tip = "Cold eggs crack in hot water. Lower them slowly and they'll stay whole.",
                ),
                CookStep("Cool the eggs in cold water", null, 120, "until you can hold them", null),
            ),
    )

private fun timerForPreview(
    step: Int,
    ringing: Boolean = false,
) = RunningTimer(
    id = "t$step",
    name = "Jammy eggs on toast • Step ${step + 1}",
    detail = null,
    durationMillis = 390_000,
    endsAtMillis = if (ringing) 0 else 252_000,
    pausedRemainingMillis = null,
    target = TimerTarget.CookStep(CookKind.Lesson, "jammy-eggs-on-toast", 0, step),
    ringing = ringing,
)

@Preview
@Composable
private fun CookContentPreview() {
    StovetimeTheme {
        CookContent(state = CookState(plan = ViewState.Success(planPreviewData()), step = 1), notificationsAllowed = true, onAction = {})
    }
}

@Preview
@Composable
private fun CookContentRunningPreview() {
    StovetimeTheme {
        CookContent(
            state = CookState(plan = ViewState.Success(planPreviewData()), step = 2, timers = listOf(timerForPreview(1), timerForPreview(2))),
            notificationsAllowed = false,
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun CookContentRingingPreview() {
    StovetimeTheme {
        CookContent(
            state = CookState(plan = ViewState.Success(planPreviewData()), step = 1, timers = listOf(timerForPreview(1, ringing = true))),
            notificationsAllowed = true,
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun CookContentErrorPreview() {
    StovetimeTheme {
        CookContent(state = CookState(plan = ViewState.Error(DataError.Unknown("missing"))), notificationsAllowed = true, onAction = {})
    }
}
