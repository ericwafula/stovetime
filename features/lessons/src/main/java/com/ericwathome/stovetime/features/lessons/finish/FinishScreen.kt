package com.ericwathome.stovetime.features.lessons.finish

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.model.progress.Rating
import com.ericwathome.stovetime.core.domain.model.Badge
import com.ericwathome.stovetime.core.domain.model.BadgeKind
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.model.WeekDay
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.design.components.StatTile
import com.ericwathome.stovetime.design.components.StatTone
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveButtonStyle
import com.ericwathome.stovetime.design.components.StoveChoice
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.lessons.R
import com.ericwathome.stovetime.ui.components.BadgeCard
import com.ericwathome.stovetime.ui.components.DishPhoto
import com.ericwathome.stovetime.ui.components.WeekRow
import com.ericwathome.stovetime.ui.components.WeekRowDay
import com.ericwathome.stovetime.ui.helpers.CollectOneTimeEvent
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.cookTimeText
import com.ericwathome.stovetime.ui.helpers.countText
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FinishRoute(
    kind: CookKind,
    dishId: String,
    startedAt: Long,
    finishedAt: Long,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FinishScreen(
        onContinue = onContinue,
        modifier = modifier,
        viewModel =
            koinViewModel(key = "finish_${kind}_${dishId}_$startedAt") {
                parametersOf(FinishArgs(kind = kind, dishId = dishId, startedAt = startedAt, finishedAt = finishedAt))
            },
    )
}

@Composable
internal fun FinishScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FinishViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            FinishEvent.Continue -> onContinue()
        }
    }

    BackHandler { viewModel.onAction(FinishAction.OnClickContinue) }

    FinishContent(state = state, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun FinishContent(
    state: FinishState,
    onAction: (FinishAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(StovetimeTheme.colors.canvas)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        when (val dish = state.dish) {
            ViewState.Initial, ViewState.Loading -> Spacer(Modifier.weight(1f))
            is ViewState.Error -> FinishError(onRetry = { onAction(FinishAction.OnClickRetry) }, modifier = Modifier.weight(1f))
            is ViewState.Success -> FinishDetails(dish = dish.data, state = state, onAction = onAction, modifier = Modifier.weight(1f))
        }
        // TODO(demo): a Share button here sends ShareCookCard as an image with a caption. The share-image skill has the steps
        StoveButton(
            text = stringResource(R.string.finish_continue),
            onClick = { onAction(FinishAction.OnClickContinue) },
            style = StoveButtonStyle.Success,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = StovetimeTheme.spacing.s24, vertical = StovetimeTheme.spacing.s16)
                    .testTag("finish_continue"),
        )
    }
}

@Composable
private fun FinishDetails(
    dish: FinishedDish,
    state: FinishState,
    onAction: (FinishAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    val colors = StovetimeTheme.colors
    val progress = state.progress
    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.s24, vertical = spacing.s24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(152.dp)
                .border(6.dp, colors.success, CircleShape)
                .padding(10.dp)
                .clip(CircleShape),
        ) {
            DishPhoto(photo = dish.photo, contentDescription = dish.name, modifier = Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(spacing.s20))
        StoveText(
            text = stringResource(R.string.finish_title),
            style = StovetimeTheme.typography.display,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        progress?.let {
            Spacer(Modifier.height(spacing.s8))
            StoveText(
                text = encouragement(state, it),
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("finish_message"),
            )
        }
        if (state.newBadges.isNotEmpty()) {
            Spacer(Modifier.height(spacing.s20))
            NewBadges(badges = state.newBadges)
        }
        Spacer(Modifier.height(spacing.s24))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s8), modifier = Modifier.fillMaxWidth()) {
            StatTile(
                value = stringResource(R.string.finish_xp_value, dish.xpEarned),
                label = stringResource(R.string.finish_xp_label),
                tone = StatTone.Xp,
                modifier = Modifier.weight(1f).testTag("finish_xp"),
            )
            StatTile(
                value = cookTimeText(dish.cookMillis),
                label = stringResource(R.string.finish_time_label),
                tone = StatTone.Success,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                value = countText(progress?.weekStreak ?: 0),
                label = stringResource(R.string.finish_streak_label),
                tone = StatTone.Primary,
                modifier = Modifier.weight(1f),
            )
        }
        progress?.let {
            Spacer(Modifier.height(spacing.s24))
            WeekRow(
                days = it.thisWeek.days.map { day -> WeekRowDay(date = day.date, cooked = day.cooked, isToday = day.isToday) },
                cooks = it.thisWeek.cooks,
                goal = it.thisWeek.goal,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(spacing.s24))
        StoveText(
            text = stringResource(R.string.finish_rating_title),
            style = StovetimeTheme.typography.label,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(spacing.s12))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.s8), modifier = Modifier.fillMaxWidth()) {
            ratings.forEach { (rating, label) ->
                StoveChoice(
                    text = stringResource(label),
                    selected = state.rating == rating,
                    onClick = { onAction(FinishAction.OnSelectRating(rating)) },
                    modifier = Modifier.weight(1f).testTag("finish_rating_${rating.name}"),
                )
            }
        }
    }
}

@Composable
private fun NewBadges(badges: List<Badge>) {
    Column(
        modifier = Modifier.fillMaxWidth().testTag("finish_new_badge"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(StovetimeTheme.spacing.s8),
    ) {
        StoveText(
            text = stringResource(R.string.finish_new_badge),
            style = StovetimeTheme.typography.heading,
            color = StovetimeTheme.colors.xpContent,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
        badges.forEach { badge ->
            when (val kind = badge.kind) {
                is BadgeKind.UnitDone -> BadgeCard(unitTitle = kind.unitTitle, streakWeeks = null, earned = true, modifier = Modifier.fillMaxWidth())
                is BadgeKind.Streak -> BadgeCard(unitTitle = null, streakWeeks = kind.weeks, earned = true, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

private val ratings =
    listOf(
        Rating.Overdone to R.string.finish_overdone,
        Rating.PrettyGood to R.string.finish_pretty_good,
        Rating.NailedIt to R.string.finish_nailed_it,
    )

@Composable
private fun encouragement(
    state: FinishState,
    progress: Progress,
): String {
    val week = progress.thisWeek
    return when {
        state.goalJustMet -> stringResource(R.string.finish_goal_met, week.cooks, week.goal)
        week.cooks > week.goal -> stringResource(R.string.finish_above_goal, week.cooks, week.goal)
        else -> stringResource(R.string.finish_keep_going, week.cooks, week.goal)
    }
}

@Composable
private fun FinishError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth().padding(StovetimeTheme.spacing.s24),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StoveText(text = stringResource(R.string.finish_error), style = StovetimeTheme.typography.heading, textAlign = TextAlign.Center)
        Spacer(Modifier.height(StovetimeTheme.spacing.s16))
        StoveButton(text = stringResource(R.string.path_retry), onClick = onRetry, style = StoveButtonStyle.Secondary)
    }
}

private fun finishStatePreview(rating: Rating?): FinishState {
    val days = (2..8).map { WeekDay(date = LocalDate(2026, 11, it), cooked = it in setOf(2, 4, 6), isToday = it == 6) }
    return FinishState(
        dish = ViewState.Success(FinishedDish(name = "Jammy eggs on toast", photo = "lesson_jammy_eggs_on_toast", xpEarned = 30, cookMillis = 840_000)),
        progress = Progress(totalXp = 240, weekStreak = 3, bestWeekStreak = 3, thisWeek = WeekProgress(days, cooks = 3, goal = 4), history = emptyList()),
        rating = rating,
    )
}

@Preview
@Composable
private fun FinishContentPreview() {
    StovetimeTheme {
        FinishContent(state = finishStatePreview(Rating.PrettyGood), onAction = {})
    }
}

@Preview
@Composable
private fun FinishContentErrorPreview() {
    StovetimeTheme {
        FinishContent(state = FinishState(dish = ViewState.Error(DataError.StorageFull)), onAction = {})
    }
}
