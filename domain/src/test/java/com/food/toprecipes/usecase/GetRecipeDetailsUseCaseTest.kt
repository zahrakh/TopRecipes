package com.food.toprecipes.usecase

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.Ingredient
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.repository.SpoonacularRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRecipeDetailsUseCaseTest {

    private lateinit var repository: SpoonacularRepository
    private lateinit var useCase: GetRecipesDetailsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetRecipesDetailsUseCase(repository)
    }

    @Test
    fun `should return Success when repository returns Success`() = runTest {
        val recipeId = "123"
        val detail = RecipeDetail(
            recipeDetailId = recipeId,
            title = "Soup",
            image = "img.png",
            sourceUrl = "https://example.com",
            instructions = "Cook it",
            ingredients = listOf(Ingredient("Salt")),
            isFavorite = false
        )
        coEvery { repository.getRecipeDetails(recipeId) } returns DomainResult.Success(detail)

        val result = useCase(recipeId)

        assertTrue(result is DomainResult.Success)
        assertEquals(detail, (result as DomainResult.Success).value)
        coVerify(exactly = 1) { repository.getRecipeDetails(recipeId) }
    }

    @Test
    fun `should return Error when repository returns Error`() = runTest {
        val recipeId = "456"
        val error = DomainError.TimeoutError(null, "Timeout")
        coEvery { repository.getRecipeDetails(recipeId) } returns DomainResult.Error(error)

        val result = useCase(recipeId)

        assertTrue(result is DomainResult.Error)
        assertEquals(error, (result as DomainResult.Error).value)
    }
}
