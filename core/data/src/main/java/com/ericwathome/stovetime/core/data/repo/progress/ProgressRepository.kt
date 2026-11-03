package com.ericwathome.stovetime.core.data.repo.progress

interface ProgressRepository {
    val saveCook: SaveCookRepository
    val rateCook: RateCookRepository
    val observeCooks: ObserveCooksRepository
    val clearProgress: ClearProgressRepository
}

internal class ProgressRepositoryImpl(
    override val saveCook: SaveCookRepository,
    override val rateCook: RateCookRepository,
    override val observeCooks: ObserveCooksRepository,
    override val clearProgress: ClearProgressRepository,
) : ProgressRepository
