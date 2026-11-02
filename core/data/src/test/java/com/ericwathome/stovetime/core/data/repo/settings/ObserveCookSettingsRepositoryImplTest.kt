package com.ericwathome.stovetime.core.data.repo.settings

import app.cash.turbine.test
import com.ericwathome.stovetime.core.data.model.settings.CookSettings
import com.ericwathome.stovetime.datasources.local.sources.settings.CookSettingsLocalDatasource
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObserveCookSettingsRepositoryImplTest {
    @MockK
    private lateinit var cookSettingsLocalDatasource: CookSettingsLocalDatasource

    private val weeklyGoal = MutableStateFlow<Int?>(null)
    private val keepScreenOn = MutableStateFlow<Boolean?>(null)
    private lateinit var sut: ObserveCookSettingsRepositoryImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        every { cookSettingsLocalDatasource.weeklyGoal.observe() } returns weeklyGoal
        every { cookSettingsLocalDatasource.keepScreenOn.observe() } returns keepScreenOn
        sut = ObserveCookSettingsRepositoryImpl(cookSettingsLocalDatasource)
    }

    @Test
    fun a_fresh_install_has_a_goal_of_four_and_keeps_the_screen_on() =
        runTest {
            sut().test {
                // then
                assertThat(awaitItem()).isEqualTo(CookSettings(weeklyGoal = 4, keepScreenOn = true))
            }
        }

    @Test
    fun a_settings_change_is_seen_straight_away() =
        runTest {
            sut().test {
                skipItems(1)

                // when
                weeklyGoal.value = 6
                keepScreenOn.value = false

                // then
                assertThat(expectMostRecentItem()).isEqualTo(CookSettings(weeklyGoal = 6, keepScreenOn = false))
            }
        }

    @Test
    fun a_stored_goal_outside_one_to_seven_is_clamped() =
        runTest {
            // given
            weeklyGoal.value = 12

            sut().test {
                // then
                assertThat(awaitItem().weeklyGoal).isEqualTo(7)
            }
        }
}
