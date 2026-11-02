package com.ericwathome.stovetime.core.domain.useCase

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.common.result.map
import com.ericwathome.stovetime.core.data.helpers.Today
import com.ericwathome.stovetime.core.data.helpers.asLocalDate
import com.ericwathome.stovetime.core.data.model.progress.Cook
import com.ericwathome.stovetime.core.data.repo.progress.ProgressRepository
import com.ericwathome.stovetime.core.data.repo.settings.SettingsRepository
import com.ericwathome.stovetime.core.domain.model.Progress
import com.ericwathome.stovetime.core.domain.rules.CookedDay
import com.ericwathome.stovetime.core.domain.rules.bestWeekStreak
import com.ericwathome.stovetime.core.domain.rules.weekProgress
import com.ericwathome.stovetime.core.domain.rules.weekStreak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate

fun interface ObserveProgressUseCase {
    operator fun invoke(): Flow<DataResult<Progress>>
}

internal class ObserveProgressUseCaseImpl(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
) : ObserveProgressUseCase {
    override fun invoke(): Flow<DataResult<Progress>> =
        combine(progressRepository.observeCooks(), settingsRepository.observeCookSettings()) { cooks, settings ->
            cooks.map { history -> progressOf(history = history, goal = settings.weeklyGoal, today = Today) }
        }
}

internal fun progressOf(
    history: List<Cook>,
    goal: Int,
    today: LocalDate,
): Progress {
    val days = history.map { CookedDay(date = it.finishedAt.asLocalDate, weeklyGoal = it.weeklyGoal) }
    return Progress(
        totalXp = history.sumOf { it.xp },
        weekStreak = weekStreak(days, today, goal),
        bestWeekStreak = bestWeekStreak(days, today, goal),
        thisWeek = weekProgress(days, today, goal),
        history = history.sortedByDescending { it.finishedAt },
    )
}
