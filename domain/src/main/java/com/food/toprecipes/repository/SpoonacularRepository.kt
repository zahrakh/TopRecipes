package com.food.toprecipes.repository

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.model.RecipesResponse

/**
 * Repository interface for Spoonacular API operations.
 * Defines the contract for fetching recipe data from the Spoonacular API.
 */
interface SpoonacularRepository {

    suspend fun getRecipes(): DomainResult<DomainError, RecipesResponse>

    suspend fun getRecipeDetails(recipeId: String): DomainResult<DomainError, RecipeDetail>
}
