package com.ericwathome.stovetime.features.lessons.home

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.data.model.course.CourseUnit
import com.ericwathome.stovetime.core.data.model.course.Difficulty
import com.ericwathome.stovetime.core.data.model.course.Lesson
import com.ericwathome.stovetime.core.domain.model.LessonState
import com.ericwathome.stovetime.core.domain.model.PathLesson
import com.ericwathome.stovetime.core.domain.model.PathProgress
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.model.WeekProgress
import com.ericwathome.stovetime.design.components.PathNode
import com.ericwathome.stovetime.design.components.PathNodeState
import com.ericwathome.stovetime.design.components.StatChip
import com.ericwathome.stovetime.design.components.StatKind
import com.ericwathome.stovetime.design.components.StoveButton
import com.ericwathome.stovetime.design.components.StoveButtonStyle
import com.ericwathome.stovetime.design.components.StoveCard
import com.ericwathome.stovetime.design.components.StoveProgressBar
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.components.StoveToast
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.features.lessons.R
import com.ericwathome.stovetime.ui.helpers.CollectOneTimeEvent
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.bundledPhotoPainter
import com.ericwathome.stovetime.ui.helpers.countText
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

private const val LOCKED_MESSAGE_MILLIS = 2_500L
private val NodeSwing = 64.dp
private val NavBarClearance = 120.dp

@Composable
fun PathRoute(
    onOpenLesson: (lessonId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    PathScreen(onOpenLesson = onOpenLesson, modifier = modifier)
}

@Composable
internal fun PathScreen(
    onOpenLesson: (lessonId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PathViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var lockedMessages by remember { mutableIntStateOf(0) }

    CollectOneTimeEvent(viewModel.event) { event ->
        when (event) {
            is PathEvent.OpenLesson -> onOpenLesson(event.lessonId)
            PathEvent.ShowLocked -> lockedMessages++
        }
    }

    Box(modifier.fillMaxSize()) {
        PathContent(state = state, onAction = viewModel::onAction)
        if (lockedMessages > 0) {
            LaunchedEffect(lockedMessages) {
                delay(LOCKED_MESSAGE_MILLIS)
                lockedMessages = 0
            }
            StoveToast(
                text = stringResource(R.string.path_locked),
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = NavBarClearance),
            )
        }
    }
}

@Composable
private fun PathContent(
    state: PathState,
    onAction: (PathAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(StovetimeTheme.colors.canvas)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        when (val path = state.path) {
            ViewState.Initial, ViewState.Loading -> Unit
            is ViewState.Error -> PathError(onRetry = { onAction(PathAction.OnClickRetry) })
            is ViewState.Success -> PathDetails(path = path.data, onAction = onAction)
        }
    }
}

@Composable
private fun PathDetails(
    path: PathProgress,
    onAction: (PathAction) -> Unit,
) {
    val spacing = StovetimeTheme.spacing
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.s24)
            .padding(top = spacing.s20, bottom = NavBarClearance),
    ) {
        PathHeader(courseTitle = path.courseTitle, streak = path.progress.weekStreak, xp = path.progress.totalXp)
        Spacer(Modifier.height(spacing.s20))
        UnitBanner(unit = path.unit, cooked = path.cookedCount, isDone = path.isUnitDone)
        Spacer(Modifier.height(spacing.s20))
        path.lessons.forEachIndexed { index, lesson ->
            LessonNode(
                lesson = lesson,
                swing = nodeSwing(index),
                onClick = { onAction(PathAction.OnClickLesson(lesson.lesson.id)) },
                modifier = Modifier.testTag("path_node_$index"),
            )
            if (lesson.state == LessonState.Current) {
                UpNextCard(
                    lesson = lesson.lesson,
                    xp = lesson.nextCookXp,
                    onStart = { onAction(PathAction.OnClickStart) },
                    modifier = Modifier.padding(vertical = spacing.s12),
                )
            }
            Spacer(Modifier.height(spacing.s12))
        }
    }
}

private fun nodeSwing(index: Int) =
    when (index % 4) {
        1 -> NodeSwing
        3 -> -NodeSwing
        else -> 0.dp
    }

@Composable
private fun PathHeader(
    courseTitle: String,
    streak: Int,
    xp: Int,
) {
    val colors = StovetimeTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        StoveText(
            text = courseTitle,
            style = StovetimeTheme.typography.label,
            modifier =
                Modifier
                    .background(colors.surface, StovetimeTheme.shapes.small)
                    .border(2.dp, colors.border, StovetimeTheme.shapes.small)
                    .padding(horizontal = 12.dp, vertical = 9.dp),
        )
        Spacer(Modifier.weight(1f))
        val streakText = countText(streak)
        val xpText = countText(xp)
        StatChip(
            kind = StatKind.Streak,
            value = streakText,
            contentDescription = stringResource(R.string.path_streak_description, streakText),
            modifier = Modifier.testTag("path_streak"),
        )
        Spacer(Modifier.padding(horizontal = 10.dp))
        StatChip(
            kind = StatKind.Xp,
            value = xpText,
            contentDescription = stringResource(R.string.path_xp_description, xpText),
            modifier = Modifier.testTag("path_xp"),
        )
    }
}

@Composable
private fun UnitBanner(
    unit: CourseUnit,
    cooked: Int,
    isDone: Boolean,
) {
    val colors = StovetimeTheme.colors
    StoveCard(color = colors.xp, ledge = colors.xpShade, bordered = false, padding = 20.dp, modifier = Modifier.fillMaxWidth()) {
        StoveText(
            text = stringResource(R.string.path_unit_eyebrow, unit.number, unit.id).uppercase(),
            style = StovetimeTheme.typography.label,
            color = colors.onXp,
        )
        Spacer(Modifier.height(4.dp))
        StoveText(text = unit.title, style = StovetimeTheme.typography.title, color = colors.onXp)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StoveProgressBar(
                progress = if (unit.lessons.isEmpty()) 0f else cooked.toFloat() / unit.lessons.size,
                track = colors.xpSoft,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.padding(horizontal = 5.dp))
            StoveText(
                text =
                    if (isDone) {
                        stringResource(R.string.path_unit_done)
                    } else {
                        stringResource(R.string.path_unit_progress, cooked, unit.lessons.size)
                    },
                style = StovetimeTheme.typography.caption,
                color = colors.onXp,
            )
        }
    }
}

@Composable
private fun LessonNode(
    lesson: PathLesson,
    swing: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = lesson.lesson.title
    val description =
        when (lesson.state) {
            LessonState.Done -> stringResource(R.string.path_node_done, title)
            LessonState.Current -> stringResource(R.string.path_node_current, title)
            LessonState.Locked -> stringResource(R.string.path_node_locked, title)
        }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().offset(x = swing),
    ) {
        PathNode(
            state =
                when (lesson.state) {
                    LessonState.Done -> PathNodeState.Done
                    LessonState.Current -> PathNodeState.Current
                    LessonState.Locked -> PathNodeState.Locked
                },
            contentDescription = description,
            photo = if (lesson.state == LessonState.Current) bundledPhotoPainter(lesson.lesson.photo) else null,
            onClick = onClick,
            modifier = modifier,
        )
        if (lesson.state != LessonState.Current) {
            StoveText(
                text = title,
                style = StovetimeTheme.typography.caption,
                color = StovetimeTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun UpNextCard(
    lesson: Lesson,
    xp: Int,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StoveCard(modifier = modifier.fillMaxWidth(), padding = 18.dp) {
        StoveText(
            text = stringResource(R.string.path_up_next, lesson.minutes),
            style = StovetimeTheme.typography.caption,
            color = StovetimeTheme.colors.textSecondary,
        )
        Spacer(Modifier.height(6.dp))
        StoveText(text = lesson.title, style = StovetimeTheme.typography.heading)
        Spacer(Modifier.height(14.dp))
        StoveButton(
            text = stringResource(R.string.path_start, xp),
            onClick = onStart,
            modifier = Modifier.testTag("path_start"),
        )
    }
}

@Composable
private fun PathError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(StovetimeTheme.spacing.s24),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StoveText(text = stringResource(R.string.path_error_title), style = StovetimeTheme.typography.heading, textAlign = TextAlign.Center)
        Spacer(Modifier.height(StovetimeTheme.spacing.s16))
        StoveButton(text = stringResource(R.string.path_retry), onClick = onRetry, style = StoveButtonStyle.Secondary)
    }
}

internal fun sampleLesson(
    id: String,
    title: String,
    xp: Int = 30,
) = Lesson(
    id = id,
    title = title,
    minutes = 15,
    difficulty = Difficulty.Easy,
    xp = xp,
    photo = "lesson_$id",
    learn = emptyList(),
    ingredients = emptyList(),
    steps = emptyList(),
)

private fun pathForPreview(cooked: Int): PathProgress {
    val lessons =
        listOf(
            sampleLesson("boiled_eggs", "Boiled eggs"),
            sampleLesson("scrambled_eggs", "Scrambled eggs"),
            sampleLesson("jammy_eggs_on_toast", "Jammy eggs on toast"),
            sampleLesson("french_omelette", "French omelette"),
            sampleLesson("shakshuka", "Shakshuka"),
        )
    return PathProgress(
        courseTitle = "Home basics",
        unit = CourseUnit(id = "eggs", number = 1, title = "Crack it: five ways with eggs", lessons = lessons),
        lessons =
            lessons.mapIndexed { index, lesson ->
                val state =
                    when {
                        index < cooked -> LessonState.Done
                        index == cooked -> LessonState.Current
                        else -> LessonState.Locked
                    }
                PathLesson(lesson = lesson, state = state, position = index + 1, cookedBefore = index < cooked, nextCookXp = lesson.xp)
            },
        progress = Progress(240, 3, 3, WeekProgress(emptyList(), 2, 4), emptyList()),
    )
}

@Preview
@Composable
private fun PathContentPreview() {
    StovetimeTheme {
        PathContent(state = PathState(path = ViewState.Success(pathForPreview(cooked = 2))), onAction = {})
    }
}

@Preview
@Composable
private fun PathContentDonePreview() {
    StovetimeTheme {
        PathContent(state = PathState(path = ViewState.Success(pathForPreview(cooked = 5))), onAction = {})
    }
}

@Preview
@Composable
private fun PathErrorPreview() {
    StovetimeTheme {
        PathContent(
            state = PathState(path = ViewState.Error(DataError.Unknown("broken"))),
            onAction = {},
        )
    }
}
