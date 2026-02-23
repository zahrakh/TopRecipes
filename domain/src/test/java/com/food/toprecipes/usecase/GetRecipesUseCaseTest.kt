package com.food.toprecipes.usecase

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.Recipe
import com.food.toprecipes.model.RecipesResponse
import com.food.toprecipes.repository.SpoonacularRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRecipesUseCaseTest {

    private lateinit var repository: SpoonacularRepository
    private lateinit var useCase: GetRecipesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetRecipesUseCase(repository)
    }

    @Test
    fun `should return Success when repository returns Success`() = runTest {
        val response = RecipesResponse(
            recipes = listOf(Recipe(id = 1, title = "Pasta", image = "url", imageType = "jpg")),
            offset = 0,
            number = 10,
            totalResults = 100
        )
        coEvery { repository.getRecipes(offset = 0, number = 10, query = "") } returns DomainResult.Success(response)

        val result = useCase(offset = 0, number = 10, query = "")

        assertTrue(result is DomainResult.Success)
        assertEquals(response, (result as DomainResult.Success).value)
        coVerify(exactly = 1) { repository.getRecipes(offset = 0, number = 10, query = "") }
    }

    @Test
    fun `should pass offset number and query to repository when invoke is called with parameters`() = runTest {
        coEvery { repository.getRecipes(offset = 20, number = 5, query = "chicken") } returns
            DomainResult.Success(RecipesResponse(emptyList(), 20, 5, 0))

        useCase(offset = 20, number = 5, query = "chicken")

        coVerify(exactly = 1) { repository.getRecipes(offset = 20, number = 5, query = "chicken") }
    }

    @Test
    fun `should return Error when repository returns Error`() = runTest {
        val error = DomainError.ServiceUnavailable(null, "Unavailable")
        coEvery { repository.getRecipes(offset = 0, number = 10, query = "") } returns DomainResult.Error(error)

        val result = useCase()

        assertTrue(result is DomainResult.Error)
        assertEquals(error, (result as DomainResult.Error).value)
    }
}
