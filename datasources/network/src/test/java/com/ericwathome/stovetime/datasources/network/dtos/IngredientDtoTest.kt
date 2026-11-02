package com.ericwathome.stovetime.datasources.network.dtos

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IngredientDtoTest {
    @Test
    fun ingredients_pair_each_ingredient_with_its_measure_and_skip_gaps() {
        // given
        val meal =
            MealDto(
                idMeal = "1",
                strMeal = "Pancakes",
                strIngredient1 = "Flour",
                strMeasure1 = "100g",
                strIngredient2 = "",
                strMeasure2 = "",
                strIngredient3 = null,
                strMeasure3 = null,
                strIngredient4 = "  Eggs ",
                strMeasure4 = " 2 ",
                strIngredient20 = "Milk",
                strMeasure20 = "300ml",
            )

        // when
        val ingredients = meal.ingredients()

        // then
        assertThat(ingredients)
            .containsExactly(
                IngredientDto(name = "Flour", measure = "100g"),
                IngredientDto(name = "Eggs", measure = "2"),
                IngredientDto(name = "Milk", measure = "300ml"),
            ).inOrder()
    }

    @Test
    fun an_ingredient_without_a_measure_keeps_an_empty_measure() {
        // given
        val meal =
            MealDto(
                idMeal = "1",
                strMeal = "Salted water",
                strIngredient1 = "Salt",
                strMeasure1 = null,
                strIngredient2 = "Water",
                strMeasure2 = " ",
            )

        // when
        val ingredients = meal.ingredients()

        // then
        assertThat(ingredients)
            .containsExactly(
                IngredientDto(name = "Salt", measure = ""),
                IngredientDto(name = "Water", measure = ""),
            ).inOrder()
    }

    @Test
    fun a_measure_without_an_ingredient_is_dropped() {
        // given
        val meal = MealDto(idMeal = "1", strMeal = "Nothing", strIngredient1 = " ", strMeasure1 = "1 tsp")

        // when
        val ingredients = meal.ingredients()

        // then
        assertThat(ingredients).isEmpty()
    }
}
