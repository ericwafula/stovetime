package com.ericwathome.stovetime.features.lessons.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.data.model.course.Difficulty
import com.ericwathome.stovetime.core.data.model.course.Lesson
import com.ericwathome.stovetime.core.domain.model.LessonState
import com.ericwathome.stovetime.core.domain.model.PathLesson
import com.ericwathome.stovetime.design.components.Pill
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveButtonStyle
import com.ericwathome.stovetime.design.components.StoveCheckRow
import com.ericwathome.stovetime.design.components.StoveIcon
import com.ericwathome.stovetime.design.components.StoveIcons
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.lessons.R
import com.ericwathome.stovetime.ui.components.BundledPhoto
import com.ericwathome.stovetime.ui.helpers.CollectOneTimeEvent
import com.ericwathome.stovetime.ui.helpers.ViewState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LessonIntroRoute(
    lessonId: String,
    onClose: () -> Unit,
    onOpenCook: (lessonId: String, startedAt: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LessonIntroScreen(
        onClose = onClose,
        onOpenCook = onOpenCook,
        modifier = modifier,
        viewModel = koinViewModel(key = "lesson_intro_$lessonId") { parametersOf(lessonId) },
    )
}

@Composable
internal fun LessonIntroScreen(
    onClose: () -> Unit,
    onOpenCook: (lessonId: String, startedAt: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LessonIntroViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            is LessonIntroEvent.OpenCook -> onOpenCook(event.lessonId, event.startedAt)
            LessonIntroEvent.Close -> onClose()
        }
    }

    LessonIntroContent(state = state, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun LessonIntroContent(
    state: LessonIntroState,
    onAction: (LessonIntroAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(StovetimeTheme.colors.canvas),
    ) {
        when (val intro = state.intro) {
            ViewState.Initial, ViewState.Loading -> Unit
            is ViewState.Error -> IntroError(onAction = onAction)
            is ViewState.Success -> IntroDetails(intro = intro.data, ticked = state.ticked, onAction = onAction)
        }
        CloseButton(
            onClick = { onAction(LessonIntroAction.OnClickClose) },
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(16.dp),
        )
    }
}

@Composable
internal fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(StovetimeTheme.colors.surface)
                .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        StoveIcon(StoveIcons.Close, contentDescription = stringResource(R.string.intro_close))
    }
}

@Composable
private fun IntroDetails(
    intro: LessonIntro,
    ticked: Set<Int>,
    onAction: (LessonIntroAction) -> Unit,
) {
    val lesson = intro.pathLesson.lesson
    val spacing = StovetimeTheme.spacing
    val colors = StovetimeTheme.colors
    val cookAgain = intro.pathLesson.cookedBefore
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            BundledPhoto(
                name = lesson.photo,
                contentDescription = lesson.title,
                modifier = Modifier.fillMaxWidth().aspectRatio(360f / 250f),
            )
            Column(Modifier.padding(horizontal = spacing.s24, vertical = spacing.s24)) {
                StoveText(
                    text = stringResource(R.string.intro_position, intro.unitNumber, intro.pathLesson.position, intro.unitSize).uppercase(),
                    style = StovetimeTheme.typography.label,
                    color = colors.primaryContent,
                )
                Spacer(Modifier.height(spacing.s12))
                StoveText(text = lesson.title, style = StovetimeTheme.typography.display, modifier = Modifier.semantics { heading() })
                Spacer(Modifier.height(spacing.s16))
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.s8)) {
                    Pill(text = stringResource(R.string.intro_minutes, lesson.minutes))
                    Pill(text = difficultyText(lesson.difficulty))
                    Pill(
                        text = stringResource(R.string.intro_xp, intro.pathLesson.nextCookXp),
                        background = colors.xpSoft,
                        color = colors.xpContent,
                    )
                }
                Spacer(Modifier.height(spacing.s20))
                LearnCard(points = lesson.learn)
                Spacer(Modifier.height(spacing.s20))
                StoveText(text = stringResource(R.string.intro_need), style = StovetimeTheme.typography.heading)
                Spacer(Modifier.height(spacing.s8))
                lesson.ingredients.forEachIndexed { index, ingredient ->
                    StoveCheckRow(
                        text = ingredient,
                        checked = index in ticked,
                        onCheckedChange = { onAction(LessonIntroAction.OnToggleIngredient(index)) },
                    )
                }
            }
        }
        StoveButton(
            text =
                if (cookAgain) {
                    stringResource(R.string.intro_cook_again, intro.pathLesson.nextCookXp)
                } else {
                    stringResource(R.string.intro_start)
                },
            onClick = { onAction(LessonIntroAction.OnClickStartCooking) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = spacing.s24, vertical = spacing.s16)
                    .testTag("intro_start"),
        )
    }
}

@Composable
private fun LearnCard(points: List<String>) {
    val colors = StovetimeTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.successSoft, StovetimeTheme.shapes.medium)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StoveText(text = stringResource(R.string.intro_learn), style = StovetimeTheme.typography.label, color = colors.successContent)
        points.forEach { point ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StoveIcon(StoveIcons.Check, contentDescription = null, tint = colors.success, size = 18.dp)
                StoveText(text = point)
            }
        }
    }
}

@Composable
private fun difficultyText(difficulty: Difficulty): String =
    stringResource(
        when (difficulty) {
            Difficulty.Easy -> R.string.intro_easy
            Difficulty.Medium -> R.string.intro_medium
            Difficulty.Hard -> R.string.intro_hard
        },
    )

@Composable
private fun IntroError(onAction: (LessonIntroAction) -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(StovetimeTheme.spacing.s24),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StoveText(text = stringResource(R.string.intro_error), style = StovetimeTheme.typography.heading, textAlign = TextAlign.Center)
        Spacer(Modifier.height(StovetimeTheme.spacing.s16))
        StoveButton(
            text = stringResource(R.string.path_retry),
            onClick = { onAction(LessonIntroAction.OnClickRetry) },
            style = StoveButtonStyle.Secondary,
        )
    }
}

private fun introForPreview(cookedBefore: Boolean) =
    LessonIntro(
        pathLesson =
            PathLesson(
                lesson =
                    Lesson(
                        id = "jammy-eggs-on-toast",
                        title = "Jammy eggs on toast",
                        minutes = 15,
                        difficulty = Difficulty.Easy,
                        xp = 30,
                        photo = "lesson_jammy_eggs_on_toast",
                        learn = listOf("Timing a soft yolk to the minute", "Stopping the cook with cold water"),
                        ingredients = listOf("2 eggs, straight from the fridge", "1 thick slice of sourdough", "Butter, salt, chilli flakes"),
                        steps = emptyList(),
                    ),
                state = LessonState.Current,
                position = 3,
                cookedBefore = cookedBefore,
                nextCookXp = if (cookedBefore) 10 else 30,
            ),
        unitNumber = 1,
        unitSize = 5,
    )

@Preview
@Composable
private fun LessonIntroContentPreview() {
    StovetimeTheme {
        LessonIntroContent(state = LessonIntroState(intro = ViewState.Success(introForPreview(false)), ticked = setOf(0)), onAction = {})
    }
}

@Preview
@Composable
private fun LessonIntroCookAgainPreview() {
    StovetimeTheme {
        LessonIntroContent(state = LessonIntroState(intro = ViewState.Success(introForPreview(true))), onAction = {})
    }
}

@Preview
@Composable
private fun LessonIntroErrorPreview() {
    StovetimeTheme {
        LessonIntroContent(state = LessonIntroState(intro = ViewState.Error(DataError.Unknown("missing"))), onAction = {})
    }
}
