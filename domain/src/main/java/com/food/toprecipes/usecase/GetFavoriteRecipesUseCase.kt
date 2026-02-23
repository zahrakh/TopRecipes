package com.food.toprecipes.usecase

import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.repository.SpoonacularRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteRecipesUseCase @Inject constructor(
    private val repository: SpoonacularRepository
) {
    operator fun invoke(): Flow<List<RecipeDetail>> = repository.getFavoriteRecipes()
}
