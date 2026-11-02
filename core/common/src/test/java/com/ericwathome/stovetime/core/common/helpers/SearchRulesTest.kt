package com.ericwathome.stovetime.core.common.helpers

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SearchRulesTest {
    @Test
    fun search_ignores_case() {
        // when
        val matches = "Chicken Handi".matchesSearch("CHICKEN")

        // then
        assertThat(matches).isTrue()
    }

    @Test
    fun search_ignores_accents_in_the_name() {
        // when
        val matches = "Crème Brûlée".matchesSearch("creme brulee")

        // then
        assertThat(matches).isTrue()
    }

    @Test
    fun search_ignores_accents_in_the_query() {
        // when
        val matches = "Pate Chinois".matchesSearch("pâté")

        // then
        assertThat(matches).isTrue()
    }

    @Test
    fun a_blank_query_matches_everything() {
        // when
        val matches = "Kedgeree".matchesSearch("  ")

        // then
        assertThat(matches).isTrue()
    }

    @Test
    fun a_different_word_does_not_match() {
        // when
        val matches = "Kedgeree".matchesSearch("lasagne")

        // then
        assertThat(matches).isFalse()
    }
}
