package com.ericwathome.stovetime.features.recipes.di

import com.ericwathome.stovetime.features.recipes.browse.RecipesBrowseViewModel
import com.ericwathome.stovetime.features.recipes.recipe.RecipeViewModel
import com.ericwathome.stovetime.core.domain.di.UseCaseModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val RecipesViewModelModule =
    module {
        viewModelOf(::RecipesBrowseViewModel)
        viewModelOf(::RecipeViewModel)
    }

val RecipesModule =
    module {
        includes(RecipesViewModelModule, UseCaseModule)
    }
