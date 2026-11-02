package com.ericwathome.stovetime.features.lessons.intro

import androidx.lifecycle.viewModelScope
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.onError
import com.ericwathome.stovetime.core.common.result.onSuccess
import com.ericwathome.stovetime.core.data.helpers.Now
import com.ericwathome.stovetime.core.domain.model.PathLesson
import com.ericwathome.stovetime.core.domain.useCase.ProgressUseCases
import com.ericwathome.stovetime.ui.helpers.MviViewModel
import com.ericwathome.stovetime.ui.helpers.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface LessonIntroAction {
    data class OnToggleIngredient(
        val index: Int,
    ) : LessonIntroAction

    data object OnClickStartCooking : LessonIntroAction

    data object OnClickClose : LessonIntroAction

    data object OnClickRetry : LessonIntroAction
}

sealed interface LessonIntroEvent {
    data class OpenCook(
        val lessonId: String,
        val startedAt: Long,
    ) : LessonIntroEvent

    data object Close : LessonIntroEvent
}

data class LessonIntro(
    val pathLesson: PathLesson,
    val unitNumber: Int,
    val unitSize: Int,
)

data class LessonIntroState(
    val intro: ViewState<LessonIntro> = ViewState.Initial,
    val ticked: Set<Int> = emptySet(),
)

class LessonIntroViewModel(
    private val lessonId: String,
    private val progressUseCases: ProgressUseCases,
) : MviViewModel<LessonIntroAction, LessonIntroState, LessonIntroEvent>(LessonIntroState()) {
    private var observeJob: Job? = null

    override fun onStateSubscription() {
        if (observeJob == null) observeLesson()
    }

    override fun onAction(action: LessonIntroAction) {
        when (action) {
            is LessonIntroAction.OnToggleIngredient -> {
                onToggleIngredient(action.index)
            }

            LessonIntroAction.OnClickStartCooking -> {
                onClickStartCooking()
            }

            LessonIntroAction.OnClickClose -> {
                sendEvent { LessonIntroEvent.Close }
            }

            LessonIntroAction.OnClickRetry -> {
                observeLesson()
            }
        }
    }

    private fun observeLesson() {
        observeJob?.cancel()
        updateState { it.copy(intro = ViewState.Loading) }
        observeJob =
            viewModelScope.launch {
                progressUseCases.observeLessonPath(lessonId).collect { result ->
                    result
                        .onSuccess { path ->
                            val lesson = path.lessons.firstOrNull { it.lesson.id == lessonId }
                            val intro =
                                lesson?.let { LessonIntro(pathLesson = it, unitNumber = path.unit.number, unitSize = path.lessons.size) }
                            updateState {
                                it.copy(
                                    intro =
                                        if (intro == null) {
                                            ViewState.Error(DataError.Unknown("No lesson $lessonId"))
                                        } else {
                                            ViewState.Success(intro)
                                        },
                                )
                            }
                        }.onError { error -> updateState { it.copy(intro = ViewState.Error(error)) } }
                }
            }
    }

    private fun onToggleIngredient(index: Int) {
        updateState { state ->
            state.copy(ticked = if (index in state.ticked) state.ticked - index else state.ticked + index)
        }
    }

    private fun onClickStartCooking() {
        if (currentState.intro !is ViewState.Success) return
        sendEvent { LessonIntroEvent.OpenCook(lessonId = lessonId, startedAt = Now.toEpochMilliseconds()) }
    }
}
