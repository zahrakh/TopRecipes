package com.food.toprecipes.remotedata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipesResponseDTO(
    @SerialName("results") val results: List<RecipesDTO> = emptyList(),
    @SerialName("offset") val offset: Int? = null,
    @SerialName("number") val number: Int? = null,
    @SerialName("totalResults") val totalResults: Int? = null
)

@Serializable
data class RecipesDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("imageType") val imageType: String? = null
)

@Serializable
data class RecipeDetailsResponseDTO(
    @SerialName("title") val title: String,
    @SerialName("image") val image: String,
    @SerialName("sourceUrl") val sourceUrl: String,
    @SerialName("instructions") val instructions: String? = null,
    @SerialName("extendedIngredients")
    val extendedIngredients: List<IngredientResponseDTO> = emptyList()
)

@Serializable
data class IngredientResponseDTO(
    @SerialName("original")
    val original: String
)