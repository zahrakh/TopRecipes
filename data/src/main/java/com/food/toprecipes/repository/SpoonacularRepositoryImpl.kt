package com.food.toprecipes.repository

import com.food.toprecipes.mapper.toDomain
import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.data.map
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.model.RecipesResponse
import com.food.toprecipes.spoonacularapi.SpoonacularRemoteDataSource
import javax.inject.Inject

// Maps DTOs from remote data source to domain models
class SpoonacularRepositoryImpl @Inject constructor(
    private val remoteDataSource: SpoonacularRemoteDataSource
) : SpoonacularRepository {

    override suspend fun getRecipes(): DomainResult<DomainError, RecipesResponse> {
        return remoteDataSource.getRecipesResponse().map { it.toDomain() }
    }

    override suspend fun getRecipeDetails(recipeId: String): DomainResult<DomainError, RecipeDetail> {
        return remoteDataSource.getRecipesDetailsResponse(recipeId).map { it.toDomain(recipeId) }
        //todo check : is t correct to pass ID as a details ID
    }
}