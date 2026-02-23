package com.food.toprecipes.mapper

import android.util.Log
import com.food.toprecipes.model.Ingredient
import com.food.toprecipes.model.Recipe
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.model.RecipesResponse
import com.food.toprecipes.remotedata.IngredientResponseDTO
import com.food.toprecipes.remotedata.RecipesDTO
import com.food.toprecipes.remotedata.RecipesResponseDTO
import com.food.toprecipes.remotedata.RecipeDetailsResponseDTO

// Converts DTOs to domain models, filters out invalid recipes
fun RecipesResponseDTO.toDomain(): RecipesResponse = RecipesResponse(
    recipes = results.mapNotNull { it.toDomain() },
    offset = offset,
    number = number,
    totalResults = totalResults
)

fun RecipesDTO.toDomain(): Recipe? = if (id != null && title != null) {
    Recipe(id = id, title = title, image = image, imageType = imageType)
} else null

fun RecipeDetailsResponseDTO.toDomain(recipeId: String): RecipeDetail {
    Log.i("Zahra", "$title: $recipeId")

    return RecipeDetail(
        title = title,
        image = image,
        sourceUrl = sourceUrl,
        instructions = instructions,
        ingredients = extendedIngredients.map { it.toDomain() },
        recipeDetailId = recipeId
    )
}

fun IngredientResponseDTO.toDomain(): Ingredient = Ingredient(original = original)
