package com.food.toprecipes.model

data class RecipesResponse(
    val recipes: List<Recipe>,
    val offset: Int?,
    val number: Int?,
    val totalResults: Int?
)

data class Recipe(
    val id: Int,
    val title: String?,
    val image: String?,
    val imageType: String?
)


data class RecipeDetail(
    val recipeDetailId: String,
    val title: String,
    val image: String,
    val sourceUrl: String,
    val instructions: String?,
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val original: String
)
