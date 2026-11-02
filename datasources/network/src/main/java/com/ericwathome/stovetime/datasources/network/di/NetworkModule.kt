package com.ericwathome.stovetime.datasources.network.di

import com.ericwathome.stovetime.datasources.network.helpers.createHttpClient
import com.ericwathome.stovetime.datasources.network.sources.recipes.GetCategoriesRemoteDatasource
import com.ericwathome.stovetime.datasources.network.sources.recipes.GetCategoriesRemoteDatasourceImpl
import com.ericwathome.stovetime.datasources.network.sources.recipes.GetMealsByCategoryRemoteDatasource
import com.ericwathome.stovetime.datasources.network.sources.recipes.GetMealsByCategoryRemoteDatasourceImpl
import com.ericwathome.stovetime.datasources.network.sources.recipes.LookupMealRemoteDatasource
import com.ericwathome.stovetime.datasources.network.sources.recipes.LookupMealRemoteDatasourceImpl
import com.ericwathome.stovetime.datasources.network.sources.recipes.RecipesRemoteDatasource
import com.ericwathome.stovetime.datasources.network.sources.recipes.RecipesRemoteDatasourceImpl
import com.ericwathome.stovetime.datasources.network.sources.recipes.SearchMealsRemoteDatasource
import com.ericwathome.stovetime.datasources.network.sources.recipes.SearchMealsRemoteDatasourceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val HttpClientModule =
    module {
        single<HttpClient> { createHttpClient(engine = CIO.create()) }
    }

internal val NetworkSourcesModule =
    module {
        // parent
        singleOf(::RecipesRemoteDatasourceImpl) {
            bind<RecipesRemoteDatasource>()
        }
        // children
        singleOf(::GetCategoriesRemoteDatasourceImpl) {
            bind<GetCategoriesRemoteDatasource>()
        }
        singleOf(::GetMealsByCategoryRemoteDatasourceImpl) {
            bind<GetMealsByCategoryRemoteDatasource>()
        }
        singleOf(::SearchMealsRemoteDatasourceImpl) {
            bind<SearchMealsRemoteDatasource>()
        }
        singleOf(::LookupMealRemoteDatasourceImpl) {
            bind<LookupMealRemoteDatasource>()
        }
    }

val NetworkModule =
    module {
        includes(HttpClientModule, NetworkSourcesModule)
    }
