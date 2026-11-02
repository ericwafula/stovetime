package com.ericwathome.stovetime.core.domain.useCase

import app.cash.turbine.test
import com.ericwathome.stovetime.core.common.result.DataError
import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.core.data.repo.course.CourseRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ObservePathUseCaseImplTest {
    @MockK
    private lateinit var courseRepository: CourseRepository

    @MockK
    private lateinit var observeProgress: ObserveProgressUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    fun a_course_that_fails_to_load_passes_its_error_through() =
        runTest {
            // given
            coEvery { courseRepository.getCourse() } returns DataResult.Error(DataError.Unknown("bad json"))

            ObservePathUseCaseImpl(courseRepository, observeProgress)().test {
                // then
                assertThat(awaitItem()).isEqualTo(DataResult.Error(DataError.Unknown("bad json")))
                awaitComplete()
            }
            verify(exactly = 0) { observeProgress() }
        }
}
