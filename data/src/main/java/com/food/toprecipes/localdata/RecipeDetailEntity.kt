package com.food.toprecipes.localdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.food.toprecipes.model.Ingredient

@Entity(tableName = "recipe_cache")
data class RecipeDetailEntity(
    @PrimaryKey
    val recipeDetailId: String,
    val title: String,
    val image: String,
    val sourceUrl: String,
    val instructions: String?,
    val ingredients: List<Ingredient>,
    val isFavorite: Boolean
)
