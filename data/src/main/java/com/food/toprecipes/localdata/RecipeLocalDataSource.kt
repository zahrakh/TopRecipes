package com.food.toprecipes.localdata

import com.food.toprecipes.localdata.mapper.toDomain
import com.food.toprecipes.localdata.mapper.toEntity
import com.food.toprecipes.model.RecipeDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface RecipeLocalDataSource {
    suspend fun getRecipeDetail(recipeId: String): RecipeDetail?
    fun getFavoriteRecipes(): Flow<List<RecipeDetail>>
    suspend fun saveRecipeDetail(detail: RecipeDetail, isFavorite: Boolean = false)
    suspend fun setFavorite(recipeId: String, isFavorite: Boolean)
}

class RecipeLocalDataSourceImpl @Inject constructor(
    private val dao: RecipeDetailDao
) : RecipeLocalDataSource {

    override suspend fun getRecipeDetail(recipeId: String): RecipeDetail? =
        dao.getById(recipeId)?.toDomain()

    override fun getFavoriteRecipes(): Flow<List<RecipeDetail>> =
        dao.getFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun saveRecipeDetail(detail: RecipeDetail, isFavorite: Boolean) {
        val existing = dao.getById(detail.recipeDetailId)
        val entity = detail.toEntity(
            isFavorite = existing?.isFavorite ?: isFavorite
        )
        dao.insert(entity)
    }

    override suspend fun setFavorite(recipeId: String, isFavorite: Boolean) {
        dao.setFavorite(recipeId, isFavorite)
    }
}

