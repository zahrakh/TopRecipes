package com.food.toprecipes.usecase

import com.food.toprecipes.repository.SpoonacularRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: SpoonacularRepository
) {
    suspend operator fun invoke(recipeId: String, isFavorite: Boolean) {
        repository.setFavorite(recipeId, isFavorite)
    }
}
