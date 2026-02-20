package com.food.toprecipes.spoonacularapi

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.remotedata.RecipeDetailsResponseDTO
import com.food.toprecipes.remotedata.RecipesResponseDTO

// Fetches data from Spoonacular API and handles network errors
interface SpoonacularRemoteDataSource {
    suspend fun getRecipesResponse(): DomainResult<DomainError, RecipesResponseDTO>
    suspend fun getRecipesDetailsResponse(id: String): DomainResult<DomainError, RecipeDetailsResponseDTO>
}