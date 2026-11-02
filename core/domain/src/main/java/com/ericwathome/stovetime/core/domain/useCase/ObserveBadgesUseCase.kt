package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.common.result.map
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.ericwathome.stovetime.core.domain.model.Badge
import com.ericwathome.stovetime.core.domain.rules.badges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun interface ObserveBadgesUseCase {
    operator fun invoke(): Flow<DataResult<List<Badge>>>
}

internal class ObserveBadgesUseCaseImpl(
    private val courseRepository: CourseRepository,
    private val observeProgress: ObserveProgressUseCase,
) : ObserveBadgesUseCase {
    override fun invoke(): Flow<DataResult<List<Badge>>> =
        flow {
            when (val courses = courseRepository.getCourses()) {
                is DataResult.Error -> {
                    emit(courses)
                }

                is DataResult.Success -> {
                    observeProgress()
                        .map { result ->
                            result.map { progress ->
                                val cooked = progress.history.filter { it.kind == CookKind.Lesson }.map { it.dishId }.toSet()
                                badges(courses.data, cooked, progress.bestWeekStreak)
                            }
                        }.collect { emit(it) }
                }
            }
        }
}
