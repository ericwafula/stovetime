package com.ericwathome.stovetime.ui.helpers

import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ShareHelpersTest {
    @get:Rule
    val cache = TemporaryFolder()

    @Test
    fun the_shared_image_goes_in_the_folder_the_file_provider_exposes() {
        val file = shareFile(cache.root)

        assertThat(file.parentFile).isEqualTo(cache.root.resolve("shared"))
        assertThat(file.parentFile?.isDirectory).isTrue()
        assertThat(file.name).isEqualTo("cook.png")
    }

    @Test
    fun sharing_again_reuses_the_same_file_instead_of_filling_the_cache() {
        val first = shareFile(cache.root)
        first.writeText("old")

        val second = shareFile(cache.root)

        assertThat(second).isEqualTo(first)
        assertThat(cache.root.resolve("shared").list()).asList().containsExactly("cook.png")
    }
}
