package com.ericwathome.stovetime.features.lessons.home

import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.common.result.onError
import com.ericwathome.stovetime.core.common.result.onSuccess
import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.ericwathome.stovetime.core.domain.model.CourseSummary
import com.ericwathome.stovetime.core.domain.model.LessonState
import com.ericwathome.stovetime.core.domain.model.PathProgress
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import com.ericwathome.stovetime.ui.helpers.ViewState
import com.ericwathome.stovetime.ui.helpers.dataOrNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface PathAction {
    data class OnClickLesson(
        val lessonId: String,
    ) : PathAction

    data object OnClickStart : PathAction

    data object OnClickRetry : PathAction

    data class OnChooseCourse(
        val courseId: String,
    ) : PathAction
}

sealed interface PathEvent {
    data class OpenLesson(
        val lessonId: String,
    ) : PathEvent

    data object ShowLocked : PathEvent
}

data class PathState(
    val path: ViewState<PathProgress> = ViewState.Initial,
    val courses: List<CourseSummary> = emptyList(),
)

class PathViewModel(
    private val progressUseCases: ProgressUseCases,
    private val courseRepository: CourseRepository,
) : MviViewModel<PathAction, PathState, PathEvent>(PathState()) {
    private var observeJob: Job? = null
    private var coursesJob: Job? = null

    override fun onStateSubscription() {
        if (observeJob == null) observePath()
        if (coursesJob == null) observeCourses()
    }

    override fun onAction(action: PathAction) {
        when (action) {
            is PathAction.OnClickLesson -> {
                onClickLesson(action.lessonId)
            }

            PathAction.OnClickStart -> {
                onClickStart()
            }

            PathAction.OnClickRetry -> {
                observePath()
            }

            is PathAction.OnChooseCourse -> {
                onChooseCourse(action.courseId)
            }
        }
    }

    private fun observePath() {
        observeJob?.cancel()
        updateState { it.copy(path = ViewState.Loading) }
        observeJob =
            viewModelScope.launch {
                progressUseCases.observePath().collect { result ->
                    result
                        .onSuccess { path -> updateState { it.copy(path = ViewState.Success(path)) } }
                        .onError { error -> updateState { it.copy(path = ViewState.Error(error)) } }
                }
            }
    }

    private fun observeCourses() {
        coursesJob =
            viewModelScope.launch {
                progressUseCases.observeCourses().collect { result ->
                    result.onSuccess { courses -> updateState { it.copy(courses = courses) } }
                }
            }
    }

    private fun onChooseCourse(courseId: String) {
        if (currentState.courses.firstOrNull { it.isChosen }?.id == courseId) return
        viewModelScope.launch { courseRepository.chooseCourse(courseId) }
    }

    private fun onClickLesson(lessonId: String) {
        val lesson = currentState.path.dataOrNull()?.lessons?.firstOrNull { it.lesson.id == lessonId } ?: return
        if (lesson.state == LessonState.Locked) {
            sendEvent { PathEvent.ShowLocked }
        } else {
            sendEvent { PathEvent.OpenLesson(lessonId) }
        }
    }

    private fun onClickStart() {
        val next = currentState.path.dataOrNull()?.nextLesson ?: return
        sendEvent { PathEvent.OpenLesson(next.lesson.id) }
    }
}
