package com.ericwathome.stovetime.core.data.repo.cook

import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.common.result.map
import com.ericwathome.stovetime.core.data.mappers.cook.toCookPlan
import com.ericwathome.stovetime.core.data.model.cook.CookPlan
import com.ericwathome.stovetime.core.data.model.cook.CookStep
import com.ericwathome.stovetime.core.data.model.course.Lesson
import com.ericwathome.stovetime.core.data.model.progress.CookKind
import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.ericwathome.stovetime.core.data.repo.recipes.RecipesRepository

fun interface GetCookPlanRepository {
    suspend operator fun invoke(
        kind: CookKind,
        dishId: String,
    ): DataResult<CookPlan>
}

internal class GetCookPlanRepositoryImpl(
    private val courseRepository: CourseRepository,
    private val recipesRepository: RecipesRepository,
) : GetCookPlanRepository {
    override suspend fun invoke(
        kind: CookKind,
        dishId: String,
    ): DataResult<CookPlan> =
        when (kind) {
            CookKind.Lesson -> lessonPlan(dishId)
            CookKind.Recipe -> recipePlan(dishId)
        }

    private suspend fun recipePlan(recipeId: String): DataResult<CookPlan> =
        recipesRepository.getRecipe(recipeId).map { recipe -> recipe.toCookPlan(xp = 0) }

    private suspend fun lessonPlan(lessonId: String): DataResult<CookPlan> =
        when (val courses = courseRepository.getCourses()) {
            is DataResult.Error -> {
                courses
            }

            is DataResult.Success -> {
                courses.data
                    .flatMap { course -> course.units.flatMap { it.lessons } }
                    .firstOrNull { it.id == lessonId }
                    ?.let { DataResult.Success(it.toCookPlan()) }
                    ?: DataResult.Error(DataError.Unknown("No lesson $lessonId"))
            }
        }
}

internal fun Lesson.toCookPlan(): CookPlan =
    CookPlan(
        kind = CookKind.Lesson,
        dishId = id,
        name = title,
        photo = photo,
        baseXp = xp,
        steps =
            steps.map { step ->
                CookStep(
                    text = step.text,
                    detail = step.detail,
                    timerSeconds = step.timer?.seconds,
                    doneness = step.timer?.doneness,
                    tip = step.tip,
                )
            },
    )
