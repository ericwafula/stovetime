package com.ericwathome.stovetime.core.data.repo.progress

import com.ericwathome.stovetime.core.common.result.DataResult
import com.ericwathome.stovetime.datasources.local.helpers.LocalResult
import com.ericwathome.stovetime.datasources.local.sources.progress.cook.CookLocalDatasource
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ClearProgressRepositoryImplTest {
    @MockK
    private lateinit var cookLocalDatasource: CookLocalDatasource

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    fun clearing_progress_only_deletes_the_cooks() =
        runTest {
            // given
            coEvery { cookLocalDatasource.deleteAllCooks() } returns LocalResult.Success(true)

            // when
            val result = ClearProgressRepositoryImpl(cookLocalDatasource)()

            // then
            assertThat(result).isEqualTo(DataResult.Success(true))
            coVerify(exactly = 1) { cookLocalDatasource.deleteAllCooks() }
            confirmVerified(cookLocalDatasource)
        }
}
