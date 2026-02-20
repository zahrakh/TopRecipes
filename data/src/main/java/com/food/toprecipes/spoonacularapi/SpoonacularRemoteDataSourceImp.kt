package com.food.toprecipes.spoonacularapi

import com.food.toprecipes.ApiService
import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.data.attempt
import com.food.toprecipes.data.mapError
import com.food.toprecipes.remotedata.DomainErrorMapper
import com.food.toprecipes.remotedata.RecipeDetailsResponseDTO
import com.food.toprecipes.remotedata.RecipesResponseDTO
import javax.inject.Inject

class SpoonacularRemoteDataSourceImp @Inject constructor(
    private val service: ApiService,
    private val errorMapper: DomainErrorMapper,
) : SpoonacularRemoteDataSource {
    override suspend fun getRecipesResponse(): DomainResult<DomainError, RecipesResponseDTO> =
        attempt { service.getRecipes() }.mapError { errorMapper.mapError(it) }

    override suspend fun getRecipesDetailsResponse(id: String): DomainResult<DomainError, RecipeDetailsResponseDTO> =
        attempt { service.getRecipesDetails(id) }.mapError { errorMapper.mapError(it) }
}





