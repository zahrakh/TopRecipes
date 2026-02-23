package com.food.toprecipes.localdata.mapper

import com.food.toprecipes.localdata.RecipeDetailEntity
import com.food.toprecipes.model.RecipeDetail

// Converts RecipeDetailEntity to RecipeDetail models and viseversa
fun RecipeDetailEntity.toDomain(): RecipeDetail = RecipeDetail(
    recipeDetailId = recipeDetailId,
    title = title,
    image = image,
    sourceUrl = sourceUrl,
    instructions = instructions,
    ingredients = ingredients,
    isFavorite = isFavorite
)

fun RecipeDetail.toEntity(isFavorite: Boolean): RecipeDetailEntity =
    RecipeDetailEntity(
        recipeDetailId = recipeDetailId,
        title = title,
        image = image,
        sourceUrl = sourceUrl,
        instructions = instructions,
        ingredients = ingredients,
        isFavorite = isFavorite,
    )