package com.ericwathome.stovetime.core.domain.useCase

interface ProgressUseCases {
    val observeProgress: ObserveProgressUseCase
    val observePath: ObservePathUseCase
    val observeLessonPath: ObserveLessonPathUseCase
    val observeCourses: ObserveCoursesUseCase
    val calculateCookXp: CalculateCookXpUseCase
}

internal class ProgressUseCasesImpl(
    override val observeProgress: ObserveProgressUseCase,
    override val observePath: ObservePathUseCase,
    override val observeLessonPath: ObserveLessonPathUseCase,
    override val observeCourses: ObserveCoursesUseCase,
    override val calculateCookXp: CalculateCookXpUseCase,
) : ProgressUseCases
