package com.food.toprecipes.usecase

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.repository.SpoonacularRepository
import javax.inject.Inject

// UseCase to get details of the recipes by recipeID
class GetRecipesDetailsUseCase @Inject constructor(
    private val repository: SpoonacularRepository
) {
    suspend operator fun invoke(recipeID: String): DomainResult<DomainError, RecipeDetail> =
        repository.getRecipeDetails(recipeID)
}
