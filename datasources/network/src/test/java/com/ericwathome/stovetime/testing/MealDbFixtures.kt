package com.ericwathome.stovetime.testing

val successfulCategoriesResponse =
    """
    {
      "categories": [
        {
          "idCategory": "1",
          "strCategory": "Beef",
          "strCategoryThumb": "https://www.themealdb.com/images/category/beef.png",
          "strCategoryDescription": "Beef is the culinary name for meat from cattle."
        },
        {
          "idCategory": "3",
          "strCategory": "Dessert",
          "strCategoryThumb": "https://www.themealdb.com/images/category/dessert.png",
          "strCategoryDescription": "Dessert is a course that concludes a meal."
        }
      ]
    }
    """.trimIndent()

val successfulMealsByCategoryResponse =
    """
    {
      "meals": [
        {
          "strMeal": "Apple Frangipan Tart",
          "strMealThumb": "https://www.themealdb.com/images/media/meals/wxywrq1468235067.jpg",
          "idMeal": "52768"
        },
        {
          "strMeal": "Bakewell tart",
          "strMealThumb": "https://www.themealdb.com/images/media/meals/wyrqqq1468233628.jpg",
          "idMeal": "52767"
        }
      ]
    }
    """.trimIndent()

val successfulMealLookupResponse =
    """
    {
      "meals": [
        {
          "idMeal": "52772",
          "strMeal": "Teriyaki Chicken Casserole",
          "strDrinkAlternate": null,
          "strCategory": "Chicken",
          "strArea": "Japanese",
          "strInstructions": "Preheat oven to 350 F.",
          "strMealThumb": "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
          "strTags": "Meat,Casserole",
          "strYoutube": "https://www.youtube.com/watch?v=4aZr5hZXP_s",
          "strIngredient1": "soy sauce",
          "strIngredient2": "water",
          "strIngredient3": "",
          "strIngredient4": " brown sugar ",
          "strIngredient5": "salt",
          "strIngredient6": null,
          "strMeasure1": "3/4 cup",
          "strMeasure2": "1/2 cup",
          "strMeasure3": "",
          "strMeasure4": " 1/4 cup ",
          "strMeasure5": " ",
          "strMeasure6": null,
          "strSource": null,
          "dateModified": null
        }
      ]
    }
    """.trimIndent()

val emptyMealsResponse =
    """
    {"meals": null}
    """.trimIndent()

val malformedMealsResponse =
    """
    {"meals": [{"strMeal": "No id"}]}
    """.trimIndent()
