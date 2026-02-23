package com.food.toprecipes.repository

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.localdata.RecipeLocalDataSource
import com.food.toprecipes.remotedata.mapper.toDomain
import com.food.toprecipes.data.map
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.model.RecipesResponse
import com.food.toprecipes.spoonacularapi.SpoonacularRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SpoonacularRepositoryImpl @Inject constructor(
    private val remoteDataSource: SpoonacularRemoteDataSource,
    private val localDataSource: RecipeLocalDataSource
) : SpoonacularRepository {

    override suspend fun getRecipes(
        offset: Int,
        number: Int,
        query: String
    ): DomainResult<DomainError, RecipesResponse> {
        return remoteDataSource.getRecipesResponse(offset = offset, number = number, query = query)
            .map { it.toDomain() }
    }

    override suspend fun getRecipeDetails(recipeId: String): DomainResult<DomainError, RecipeDetail> {
        val cached = localDataSource.getRecipeDetail(recipeId)
        if (cached != null) {
            return DomainResult.Success(cached)
        }
        return when (val result = remoteDataSource.getRecipesDetailsResponse(recipeId)) {
            is DomainResult.Success -> {
                val detail = result.value.toDomain(recipeId)
                val existing = localDataSource.getRecipeDetail(recipeId)
                localDataSource.saveRecipeDetail(detail, isFavorite = existing?.isFavorite ?: false)
                DomainResult.Success(detail.copy(isFavorite = existing?.isFavorite ?: false))
            }

            is DomainResult.Error -> result
        }
    }

    override suspend fun setFavorite(recipeId: String, isFavorite: Boolean) {
        localDataSource.setFavorite(recipeId, isFavorite)
    }

    override fun getFavoriteRecipes(): Flow<List<RecipeDetail>> =
        localDataSource.getFavoriteRecipes()
}
