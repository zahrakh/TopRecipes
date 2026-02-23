package com.food.toprecipes.repository

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.model.RecipesResponse
import kotlinx.coroutines.flow.Flow


//Repository interface for Spoonacular API operations and local cache.
// Recipe details are cached for offline viewing; favorites are stored locally.

interface SpoonacularRepository {

    suspend fun getRecipes(
        offset: Int = 0,
        number: Int = 10,
        query: String = ""
    ): DomainResult<DomainError, RecipesResponse>

    //Returns cached detail if available, otherwise fetches from API and caches.
    suspend fun getRecipeDetails(recipeId: String): DomainResult<DomainError, RecipeDetail>

    suspend fun setFavorite(recipeId: String, isFavorite: Boolean)

    fun getFavoriteRecipes(): Flow<List<RecipeDetail>>
}
