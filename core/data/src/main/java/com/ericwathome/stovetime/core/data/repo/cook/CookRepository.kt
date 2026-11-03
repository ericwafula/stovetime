package com.ericwathome.stovetime.core.data.repo.cook

interface CookRepository {
    val getCookPlan: GetCookPlanRepository
}

internal class CookRepositoryImpl(
    override val getCookPlan: GetCookPlanRepository,
) : CookRepository
