package com.ericwathome.stovetime.core.data.mappers.cook

import com.ericwathome.stovetime.core.data.model.recipes.Recipe
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecipeStepMappersTest {
    @Test
    fun instructions_split_on_line_breaks() {
        // when
        val steps = splitInstructions("Heat the oil.\r\nAdd the onions and fry.\r\n\r\nServe hot.")

        // then
        assertThat(steps).containsExactly("Heat the oil.", "Add the onions and fry.", "Serve hot.").inOrder()
    }

    @Test
    fun one_paragraph_splits_on_sentences() {
        // when
        val steps = splitInstructions("Preheat the oven. Mix the flour and sugar! Bake until golden.")

        // then
        assertThat(steps).containsExactly("Preheat the oven.", "Mix the flour and sugar!", "Bake until golden.").inOrder()
    }

    @Test
    fun a_paragraph_with_no_full_stops_is_one_step() {
        // when
        val steps = splitInstructions("mix everything together and cook it slowly with love")

        // then
        assertThat(steps).containsExactly("mix everything together and cook it slowly with love")
    }

    @Test
    fun numbered_lines_lose_their_numbers() {
        // when
        val steps = splitInstructions("1. Chop the garlic\nSTEP 2\nFry it gently\nStep 3: Add the rice\n4) Stir")

        // then
        assertThat(steps).containsExactly("Chop the garlic", "Fry it gently", "Add the rice", "Stir").inOrder()
    }

    @Test
    fun minutes_become_a_timer() {
        // then
        assertThat(timerSecondsIn("Simmer for 20 minutes until thick")).isEqualTo(1_200)
        assertThat(timerSecondsIn("Rest 5 mins")).isEqualTo(300)
    }

    @Test
    fun an_hour_becomes_a_timer() {
        // then
        assertThat(timerSecondsIn("Roast for 1 hour")).isEqualTo(3_600)
        assertThat(timerSecondsIn("Braise for 2 hrs")).isEqualTo(7_200)
    }

    @Test
    fun an_hour_and_a_half_becomes_a_timer() {
        // then
        assertThat(timerSecondsIn("Cook for 1½ hours, stirring now and then")).isEqualTo(5_400)
        assertThat(timerSecondsIn("Cook for 1.5 hours")).isEqualTo(5_400)
    }

    @Test
    fun a_range_uses_the_lower_number() {
        // then
        assertThat(timerSecondsIn("Bake for 30-40 mins")).isEqualTo(1_800)
        assertThat(timerSecondsIn("Bake for 30 to 40 minutes")).isEqualTo(1_800)
    }

    @Test
    fun the_first_time_in_a_step_becomes_the_timer() {
        // then
        assertThat(timerSecondsIn("Bake 10 minutes then rest 5 minutes")).isEqualTo(600)
    }

    @Test
    fun a_step_with_no_time_has_no_timer() {
        // then
        assertThat(timerSecondsIn("Season to taste and serve")).isNull()
        assertThat(timerSecondsIn("Add minutes of love")).isNull()
    }

    @Test
    fun a_recipe_becomes_a_cook_plan_with_timers() {
        // given
        val recipe =
            Recipe(
                id = "52772",
                name = "Teriyaki Chicken Casserole",
                category = "Chicken",
                area = "Japanese",
                photo = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
                instructions = "Preheat oven to 350.\r\nBake for 35 minutes.",
                ingredients = emptyList(),
            )

        // when
        val plan = recipe.toCookPlan(xp = 10)

        // then
        assertThat(plan.steps.map { it.timerSeconds }).containsExactly(null, 2_100).inOrder()
        assertThat(plan.photo).isEqualTo(recipe.photo)
    }
}
