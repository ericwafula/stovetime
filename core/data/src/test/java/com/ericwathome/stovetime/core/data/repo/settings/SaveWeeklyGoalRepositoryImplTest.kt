package com.ericwathome.stovetime.core.data.repo.settings

import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.sources.settings.CookSettingsLocalDatasource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveWeeklyGoalRepositoryImplTest {
    @MockK
    private lateinit var cookSettingsLocalDatasource: CookSettingsLocalDatasource
    private lateinit var sut: SaveWeeklyGoalRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        coEvery { cookSettingsLocalDatasource.weeklyGoal.save(any()) } returns LocalResult.Success(true)
        sut = SaveWeeklyGoalRepositoryImpl(cookSettingsLocalDatasource)
    }

    @Test
    fun the_goal_cannot_go_below_one() =
        runTest {
            // when
            sut(0)

            // then
            coVerify { cookSettingsLocalDatasource.weeklyGoal.save(1) }
        }

    @Test
    fun the_goal_cannot_go_above_seven() =
        runTest {
            // when
            sut(9)

            // then
            coVerify { cookSettingsLocalDatasource.weeklyGoal.save(7) }
        }
}
