package com.food.toprecipes.usecase

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.RecipesResponse
import com.food.toprecipes.repository.SpoonacularRepository
import javax.inject.Inject

// UseCase to get list of the recipes
class GetRecipesUseCase @Inject constructor(
    private val repository: SpoonacularRepository
) {
    suspend operator fun invoke(
        offset: Int = 0,
        number: Int = 10
    ): DomainResult<DomainError, RecipesResponse> =
        repository.getRecipes(offset = offset, number = number)
}
