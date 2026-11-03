package com.ericwathome.stovetime.datasources.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CategoriesResponseDto(
    val categories: List<CategoryDto>? = null,
)

@Serializable
data class CategoryDto(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String? = null,
    val strCategoryDescription: String? = null,
)
