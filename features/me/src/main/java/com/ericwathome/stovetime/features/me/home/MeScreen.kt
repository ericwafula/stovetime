package com.ericwathome.stovetime.features.me.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import com.ericwathome.stovetime.core.data.model.settings.CookSettings
import com.ericwathome.stovetime.design.components.StoveStepper
import com.ericwathome.stovetime.design.components.StoveSwitch
import com.ericwathome.stovetime.design.components.StoveToast
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.data.helpers.DeviceTimeZone
import com.ericwathome.stovetime.core.data.helpers.asLocalDate
import com.ericwathome.stovetime.core.data.model.progress.Cook
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.progress.Rating
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.model.WeekDay
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.design.components.Pill
import com.ericwathome.stovetime.design.components.StatTile
import com.ericwathome.stovetime.design.components.StatTone
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveButtonStyle
import com.ericwathome.stovetime.design.components.StoveCard
import com.ericwathome.stovetime.design.components.StoveNavBarClearance
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.me.R
import com.ericwathome.stovetime.ui.components.BundledPhoto
import com.ericwathome.stovetime.ui.components.RemotePhoto
import com.ericwathome.stovetime.ui.components.WeekRow
import com.ericwathome.stovetime.ui.components.WeekRowDay
import com.ericwathome.stovetime.ui.helpers.CollectOneTimeEvent
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.countText
import com.ericwathome.stovetime.ui.helpers.dayText
import com.ericwathome.stovetime.ui.helpers.monthText
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import org.koin.compose.viewmodel.koinViewModel

private const val TOAST_MILLIS = 2_500L
private val HistoryPhotoSize = 48.dp

@Composable
fun MeRoute(
    onOpenLesson: (lessonId: String) -> Unit,
    onOpenRecipe: (recipeId: String) -> Unit,
    onOpenPath: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MeScreen(
        onOpenLesson = onOpenLesson,
        onOpenRecipe = onOpenRecipe,
        onOpenPath = onOpenPath,
        modifier = modifier,
    )
}

@Composable
internal fun MeScreen(
    onOpenLesson: (lessonId: String) -> Unit,
    onOpenRecipe: (recipeId: String) -> Unit,
    onOpenPath: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var saveFailures by remember { mutableIntStateOf(0) }

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            is MeEvent.OpenLesson -> onOpenLesson(event.lessonId)
            is MeEvent.OpenRecipe -> onOpenRecipe(event.recipeId)
            MeEvent.OpenPath -> onOpenPath()
            MeEvent.ShowSaveFailed -> saveFailures++
        }
    }

    Box(modifier.fillMaxSize()) {
        MeContent(state = state, onAction = viewModel::onAction)
        if (saveFailures > 0) {
            LaunchedEffect(saveFailures) {
                delay(TOAST_MILLIS)
                saveFailures = 0
            }
            StoveToast(
                text = stringResource(R.string.me_save_failed),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = StoveNavBarClearance),
            )
        }
    }
}

@Composable
private fun MeContent(
    state: MeState,
    onAction: (MeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(StovetimeTheme.colors.canvas)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        when (val progress = state.progress) {
            ViewState.Initial, ViewState.Loading -> Unit
            is ViewState.Error -> MeError(onRetry = { onAction(MeAction.OnClickRetry) })
            is ViewState.Success -> MeDetails(state = state, progress = progress.data, onAction = onAction)
        }
    }
}

@Composable
private fun MeDetails(
    state: MeState,
    progress: Progress,
    onAction: (MeAction) -> Unit,
) {
    val spacing = StovetimeTheme.spacing
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.s24)
                .padding(top = spacing.s12, bottom = StoveNavBarClearance + bottomInset),
        verticalArrangement = Arrangement.spacedBy(spacing.s20),
    ) {
        MeHeader(cookingSince = state.cookingSince, today = state.today)
        MeStats(progress = progress)
        WeekRow(
            days = progress.thisWeek.days.map { WeekRowDay(date = it.date, cooked = it.cooked, isToday = it.isToday) },
            cooks = progress.thisWeek.cooks,
            goal = progress.thisWeek.goal,
            modifier = Modifier.fillMaxWidth(),
        )
        state.settings?.let { settings ->
            SectionHeading(text = stringResource(R.string.me_settings))
            SettingsCard(
                settings = settings,
                canLowerGoal = state.canLowerGoal,
                canRaiseGoal = state.canRaiseGoal,
                onAction = onAction,
            )
        }
        SectionHeading(text = stringResource(R.string.me_history))
        if (progress.history.isEmpty()) {
            EmptyHistory(onGoToPath = { onAction(MeAction.OnClickGoToPath) })
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s12)) {
                progress.history.forEachIndexed { index, cook ->
                    HistoryItem(
                        cook = cook,
                        today = state.today,
                        onClick = { onAction(MeAction.OnClickCook(cook.id)) },
                        modifier = Modifier.testTag("me_history_$index"),
                    )
                }
            }
        }
    }
}

@Composable
private fun MeHeader(
    cookingSince: LocalDate?,
    today: LocalDate,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        StoveText(
            text = stringResource(R.string.me_title),
            style = StovetimeTheme.typography.title,
            modifier = Modifier.semantics { heading() },
        )
        StoveText(
            text =
                if (cookingSince == null) {
                    stringResource(R.string.me_nothing_cooked_yet)
                } else {
                    stringResource(R.string.me_cooking_since, monthText(cookingSince, today))
                },
            style = StovetimeTheme.typography.caption,
            color = StovetimeTheme.colors.textSecondary,
        )
    }
}

@Composable
private fun MeStats(progress: Progress) {
    val spacing = StovetimeTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.s8)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                value = countText(progress.totalXp),
                label = stringResource(R.string.me_total_xp),
                tone = StatTone.Xp,
                modifier = Modifier.weight(1f).testTag("me_total_xp"),
            )
            StatTile(
                value = countText(progress.weekStreak),
                label = stringResource(R.string.me_week_streak),
                tone = StatTone.Success,
                modifier = Modifier.weight(1f).testTag("me_week_streak"),
            )
            StatTile(
                value = countText(progress.cookCount),
                label = stringResource(R.string.me_dishes_cooked),
                tone = StatTone.Primary,
                modifier = Modifier.weight(1f).testTag("me_dishes_cooked"),
            )
        }
        StoveText(
            text = pluralStringResource(R.plurals.me_best_streak, progress.bestWeekStreak, progress.bestWeekStreak),
            style = StovetimeTheme.typography.caption,
            color = StovetimeTheme.colors.textSecondary,
            modifier = Modifier.testTag("me_best_streak"),
        )
    }
}

@Composable
private fun SettingsCard(
    settings: CookSettings,
    canLowerGoal: Boolean,
    canRaiseGoal: Boolean,
    onAction: (MeAction) -> Unit,
) {
    val spacing = StovetimeTheme.spacing
    StoveCard(shape = StovetimeTheme.shapes.medium, padding = 0.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = spacing.s16, vertical = spacing.s4)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.s12),
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    StoveText(text = stringResource(R.string.me_weekly_goal), style = StovetimeTheme.typography.bodyStrong)
                    StoveText(
                        text = stringResource(R.string.me_weekly_goal_hint),
                        style = StovetimeTheme.typography.caption,
                        color = StovetimeTheme.colors.textSecondary,
                    )
                }
                StoveStepper(
                    value = countText(settings.weeklyGoal),
                    onDecrease = { onAction(MeAction.OnClickLowerGoal) },
                    onIncrease = { onAction(MeAction.OnClickRaiseGoal) },
                    decreaseLabel = stringResource(R.string.me_weekly_goal_lower),
                    increaseLabel = stringResource(R.string.me_weekly_goal_raise),
                    canDecrease = canLowerGoal,
                    canIncrease = canRaiseGoal,
                    decreaseModifier = Modifier.testTag("me_goal_minus"),
                    increaseModifier = Modifier.testTag("me_goal_plus"),
                )
            }
            Box(Modifier.fillMaxWidth().height(2.dp).background(StovetimeTheme.colors.border))
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .toggleable(
                            value = settings.keepScreenOn,
                            role = Role.Switch,
                            onValueChange = { onAction(MeAction.OnKeepScreenOnChange(it)) },
                        ).padding(vertical = spacing.s12)
                        .testTag("me_keep_screen_on"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.s12),
            ) {
                StoveText(
                    text = stringResource(R.string.me_keep_screen_on),
                    style = StovetimeTheme.typography.bodyStrong,
                    modifier = Modifier.weight(1f),
                )
                StoveSwitch(checked = settings.keepScreenOn, onCheckedChange = null)
            }
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    StoveText(text = text, style = StovetimeTheme.typography.heading, modifier = Modifier.semantics { heading() })
}

@Composable
private fun HistoryItem(
    cook: Cook,
    today: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    val day = dayText(cook.finishedAt.asLocalDate, today)
    val rating = cook.rating?.let { stringResource(it.label) }
    val caption = if (rating == null) day else stringResource(R.string.me_history_caption, day, rating)
    val xp = stringResource(R.string.me_history_xp, cook.xp)
    val description = stringResource(R.string.me_history_description, cook.name, caption, xp)
    StoveCard(
        shape = StovetimeTheme.shapes.medium,
        padding = spacing.s12,
        onClick = onClick,
        modifier = modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = description },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.s12)) {
            HistoryPhoto(cook = cook)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                StoveText(text = cook.name, style = StovetimeTheme.typography.bodyStrong, maxLines = 2)
                StoveText(text = caption, style = StovetimeTheme.typography.caption, color = StovetimeTheme.colors.textSecondary, maxLines = 1)
            }
            Pill(text = xp, background = StovetimeTheme.colors.xpSoft, color = StovetimeTheme.colors.xpShade)
        }
    }
}

@Composable
private fun HistoryPhoto(cook: Cook) {
    val modifier = Modifier.size(HistoryPhotoSize).clip(StovetimeTheme.shapes.small)
    when (cook.kind) {
        CookKind.Lesson -> BundledPhoto(name = cook.photo, contentDescription = null, modifier = modifier)
        CookKind.Recipe -> RemotePhoto(url = cook.photo, contentDescription = null, modifier = modifier)
    }
}

private val Rating.label: Int
    get() =
        when (this) {
            Rating.Overdone -> R.string.me_rating_overdone
            Rating.PrettyGood -> R.string.me_rating_pretty_good
            Rating.NailedIt -> R.string.me_rating_nailed_it
        }

@Composable
private fun EmptyHistory(onGoToPath: () -> Unit) {
    val spacing = StovetimeTheme.spacing
    StoveCard(shape = StovetimeTheme.shapes.medium, padding = spacing.s20, modifier = Modifier.fillMaxWidth()) {
        StoveText(text = stringResource(R.string.me_history_empty_title), style = StovetimeTheme.typography.bodyStrong)
        Spacer(Modifier.height(spacing.s4))
        StoveText(
            text = stringResource(R.string.me_history_empty_message),
            style = StovetimeTheme.typography.caption,
            color = StovetimeTheme.colors.textSecondary,
        )
        Spacer(Modifier.height(spacing.s16))
        StoveButton(
            text = stringResource(R.string.me_history_empty_action),
            onClick = onGoToPath,
            style = StoveButtonStyle.Secondary,
            modifier = Modifier.fillMaxWidth().testTag("me_go_to_path"),
        )
    }
}

@Composable
private fun MeError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(StovetimeTheme.spacing.s24),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StoveText(text = stringResource(R.string.me_error_title), style = StovetimeTheme.typography.heading, textAlign = TextAlign.Center)
        Spacer(Modifier.height(StovetimeTheme.spacing.s16))
        StoveButton(text = stringResource(R.string.me_retry), onClick = onRetry, style = StoveButtonStyle.Secondary)
    }
}

private val PreviewToday = LocalDate(2026, 11, 6)

private fun previewCook(
    id: Long,
    kind: CookKind,
    name: String,
    photo: String?,
    day: Int,
    xp: Int,
    rating: Rating?,
) = Cook(
    id = id,
    dishId = name,
    kind = kind,
    name = name,
    photo = photo,
    startedAt = 0,
    finishedAt = LocalDate(2026, 11, day).toEpochMillisAtNoon(),
    xp = xp,
    rating = rating,
    weeklyGoal = 4,
)

private fun LocalDate.toEpochMillisAtNoon(): Long = atTime(12, 0).toInstant(DeviceTimeZone).toEpochMilliseconds()

private fun previewProgress(history: List<Cook>) =
    Progress(
        totalXp = history.sumOf { it.xp },
        weekStreak = if (history.isEmpty()) 0 else 3,
        bestWeekStreak = if (history.isEmpty()) 0 else 5,
        thisWeek =
            WeekProgress(
                days = (2..8).map { WeekDay(date = LocalDate(2026, 11, it), cooked = it in setOf(2, 4, 6), isToday = it == 6) },
                cooks = if (history.isEmpty()) 0 else 3,
                goal = 4,
            ),
        history = history,
    )

@Preview
@Composable
private fun MeContentPreview() {
    StovetimeTheme {
        MeContent(
            state =
                MeState(
                    progress =
                        ViewState.Success(
                            previewProgress(
                                listOf(
                                    previewCook(3, CookKind.Lesson, "Jammy eggs on toast", "lesson_jammy_eggs_on_toast", 6, 30, Rating.PrettyGood),
                                    previewCook(2, CookKind.Recipe, "Spaghetti Bolognese", null, 4, 10, Rating.NailedIt),
                                    previewCook(1, CookKind.Lesson, "Scrambled eggs", "lesson_scrambled_eggs", 2, 20, null),
                                ),
                            ),
                        ),
                    today = PreviewToday,
                    settings = CookSettings(),
                ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun MeContentEmptyPreview() {
    StovetimeTheme {
        MeContent(state = MeState(progress = ViewState.Success(previewProgress(emptyList())), today = PreviewToday), onAction = {})
    }
}

@Preview
@Composable
private fun MeContentErrorPreview() {
    StovetimeTheme {
        MeContent(state = MeState(progress = ViewState.Error(DataError.Unknown("broken"))), onAction = {})
    }
}
