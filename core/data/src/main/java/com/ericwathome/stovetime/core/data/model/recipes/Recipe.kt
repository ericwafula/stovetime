package com.ericwathome.stovetime.core.data.model.recipes

const val RECIPE_COOK_XP = 10

data class Recipe(
    val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val photo: String?,
    val instructions: String,
    val ingredients: List<Ingredient>,
)

data class Ingredient(
    val name: String,
    val measure: String,
)
