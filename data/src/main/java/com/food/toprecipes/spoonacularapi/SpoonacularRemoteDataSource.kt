package com.food.toprecipes.spoonacularapi

import com.food.toprecipes.domain.data.DomainError
import com.food.toprecipes.domain.data.DomainResult
import com.food.toprecipes.remotedata.RecipeDetailsResponseDTO
import com.food.toprecipes.remotedata.RecipesResponseDTO


interface SpoonacularRemoteDataSource {
    suspend fun getRecipesResponse(): DomainResult<DomainError, RecipesResponseDTO>
    suspend fun getRecipesDetailsResponse(id: String): DomainResult<DomainError, RecipeDetailsResponseDTO>
}