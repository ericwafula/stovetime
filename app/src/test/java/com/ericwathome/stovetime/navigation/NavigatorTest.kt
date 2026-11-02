package com.ericwathome.stovetime.navigation

import androidx.navigation3.runtime.NavKey
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class NavigatorTest {
    private lateinit var backStack: MutableList<NavKey>
    private lateinit var sut: Navigator

    @Before
    fun setup() {
        backStack = mutableListOf(Destination.Path)
        sut = Navigator(backStack, TopLevelTab.destinations)
    }

    @Test
    fun the_app_starts_on_path() {
        // then
        assertThat(sut.currentTab).isEqualTo(Destination.Path)
        assertThat(sut.isOnTab).isTrue()
    }

    @Test
    fun selecting_a_tab_opens_it_on_top_of_path() {
        // when
        sut.selectTab(Destination.Recipes)

        // then
        assertThat(backStack).containsExactly(Destination.Path, Destination.Recipes).inOrder()
        assertThat(sut.currentTab).isEqualTo(Destination.Recipes)
    }

    @Test
    fun switching_tabs_never_stacks_them() {
        // when
        sut.selectTab(Destination.Recipes)
        sut.selectTab(Destination.Timers)
        sut.selectTab(Destination.Me)

        // then
        assertThat(backStack).containsExactly(Destination.Path, Destination.Me).inOrder()
    }

    @Test
    fun back_from_another_tab_returns_to_path() {
        // given
        sut.selectTab(Destination.Timers)

        // when
        sut.backward()

        // then
        assertThat(backStack).containsExactly(Destination.Path)
        assertThat(sut.currentTab).isEqualTo(Destination.Path)
    }

    @Test
    fun back_on_path_leaves_the_back_stack_alone() {
        // when
        sut.backward()

        // then
        assertThat(backStack).containsExactly(Destination.Path)
    }

    @Test
    fun selecting_path_from_another_tab_pops_back_to_it() {
        // given
        sut.selectTab(Destination.Me)

        // when
        sut.selectTab(Destination.Path)

        // then
        assertThat(backStack).containsExactly(Destination.Path)
    }

    @Test
    fun opening_a_recipe_from_recipes_hides_the_tab_bar_and_back_returns_to_recipes() {
        // given
        sut.selectTab(Destination.Recipes)

        // when
        sut.navigate(Destination.Recipe("52772"))

        // then
        assertThat(sut.isOnTab).isFalse()
        assertThat(sut.currentTab).isEqualTo(Destination.Recipes)
        sut.backward()
        assertThat(backStack).containsExactly(Destination.Path, Destination.Recipes).inOrder()
    }
}
