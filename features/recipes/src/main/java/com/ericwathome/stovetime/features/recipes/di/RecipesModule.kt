package com.ericwathome.stovetime.features.recipes.di

import com.ericwathome.stovetime.features.recipes.browse.RecipesBrowseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val RecipesViewModelModule =
    module {
        viewModelOf(::RecipesBrowseViewModel)
    }

val RecipesModule =
    module {
        includes(RecipesViewModelModule)
    }
