package com.ericwathome.stovetime.ui.launchers.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.ericwathome.stovetime.design.components.StoveBottomSheet
import com.ericwathome.stovetime.design.components.StoveCard
import com.ericwathome.stovetime.design.components.StoveIcon
import com.ericwathome.stovetime.design.components.StoveIcons
import com.ericwathome.stovetime.design.components.StoveText
import com.ericwathome.stovetime.design.theme.StovetimeTheme
import com.ericwathome.stovetime.ui.R
import kotlinx.coroutines.launch

@Immutable
data class CourseOption(
    val id: String,
    val title: String,
    val cookedCount: Int,
    val lessonCount: Int,
    val isCurrent: Boolean,
)

class CourseLauncher internal constructor(
    private val onLaunch: () -> Unit,
) {
    fun launch() {
        onLaunch()
    }
}

@Composable
fun rememberCourseLauncher(
    courses: List<CourseOption>,
    onChoose: (courseId: String) -> Unit,
): CourseLauncher {
    var isVisible by rememberSaveable { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val currentOnChoose by rememberUpdatedState(onChoose)

    if (isVisible) {
        StoveBottomSheet(onDismiss = { isVisible = false }) {
            CourseLauncherContent(
                courses = courses,
                onChoose = { courseId ->
                    isVisible = false
                    currentOnChoose(courseId)
                },
            )
        }
    }

    return remember {
        CourseLauncher(
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
private fun CourseLauncherContent(
    courses: List<CourseOption>,
    onChoose: (courseId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = StovetimeTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(spacing.s12),
    ) {
        StoveText(
            text = stringResource(R.string.course_sheet_title),
            style = StovetimeTheme.typography.title,
            modifier = Modifier.fillMaxWidth().semantics { heading() },
        )
        courses.forEach { course ->
            CourseRow(course = course, onClick = { onChoose(course.id) })
        }
    }
}

@Composable
private fun CourseRow(
    course: CourseOption,
    onClick: () -> Unit,
) {
    val colors = StovetimeTheme.colors
    val currentLabel = stringResource(R.string.course_current)
    StoveCard(
        color = if (course.isCurrent) colors.successSoft else colors.surface,
        ledge = if (course.isCurrent) colors.success else colors.border,
        shape = StovetimeTheme.shapes.medium,
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag("course_option_${course.id}")
                .semantics {
                    selected = course.isCurrent
                    if (course.isCurrent) stateDescription = currentLabel
                },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                StoveText(text = course.title, style = StovetimeTheme.typography.heading)
                StoveText(
                    text = stringResource(R.string.course_progress, course.cookedCount, course.lessonCount),
                    style = StovetimeTheme.typography.caption,
                    color = colors.textSecondary,
                )
            }
            if (course.isCurrent) {
                StoveIcon(icon = StoveIcons.Check, contentDescription = null, tint = colors.success)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF6EC)
@Composable
private fun CourseLauncherContentPreview() {
    StovetimeTheme {
        CourseLauncherContent(
            courses =
                listOf(
                    CourseOption("home-basics", "Home basics", cookedCount = 2, lessonCount = 5, isCurrent = true),
                    CourseOption("rice-and-grains", "Rice & grains", cookedCount = 0, lessonCount = 4, isCurrent = false),
                    CourseOption("one-pan-dinners", "One-pan dinners", cookedCount = 1, lessonCount = 4, isCurrent = false),
                    CourseOption("kenyan-favourites", "Kenyan favourites", cookedCount = 4, lessonCount = 4, isCurrent = false),
                ),
            onChoose = {},
        )
    }
}
