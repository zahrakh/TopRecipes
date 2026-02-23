package com.food.toprecipes.spoonacularapi

import com.food.toprecipes.ApiService
import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.remotedata.DomainErrorMapper
import com.food.toprecipes.remotedata.RecipeDetailsResponseDTO
import com.food.toprecipes.remotedata.RecipesResponseDTO
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpoonacularRemoteDataSourceImpTest {

    private lateinit var apiService: ApiService
    private lateinit var errorMapper: DomainErrorMapper
    private lateinit var dataSource: SpoonacularRemoteDataSourceImp

    @Before
    fun setup() {
        apiService = mockk()
        errorMapper = mockk()
        dataSource = SpoonacularRemoteDataSourceImp(apiService, errorMapper)
    }

    @Test
    fun `should return Success with RecipesResponseDTO when getRecipesResponse succeeds`() = runTest {
        val dto = RecipesResponseDTO(
            results = emptyList(),
            offset = 0,
            number = 10,
            totalResults = 0
        )
        coEvery { apiService.getRecipes(offset = 0, number = 10, query = "") } returns dto

        val result = dataSource.getRecipesResponse(offset = 0, number = 10, query = "")

        assertTrue(result is DomainResult.Success)
        assertEquals(dto, (result as DomainResult.Success).value)
    }

    @Test
    fun `should return Error when getRecipesResponse throws and errorMapper maps it`() = runTest {
        val throwable = RuntimeException("error")
        val domainError = DomainError.UnknownIssue(null, "mapped")
        coEvery { apiService.getRecipes(offset = 0, number = 10, query = "") } throws throwable
        coEvery { errorMapper.mapError(throwable) } returns domainError

        val result = dataSource.getRecipesResponse(offset = 0, number = 10, query = "")

        assertTrue(result is DomainResult.Error)
        assertEquals(domainError, (result as DomainResult.Error).value)
    }

    @Test
    fun `should return Success with RecipeDetailsResponseDTO when getRecipesDetailsResponse succeeds`() = runTest {
        val dto = RecipeDetailsResponseDTO(
            title = "Pizza",
            image = "img.png",
            sourceUrl = "https://example.com",
            instructions = null,
            extendedIngredients = emptyList()
        )
        coEvery { apiService.getRecipesDetails("123") } returns dto

        val result = dataSource.getRecipesDetailsResponse("123")

        assertTrue(result is DomainResult.Success)
        assertEquals(dto, (result as DomainResult.Success).value)
    }

    @Test
    fun `should return Error when getRecipesDetailsResponse throws`() = runTest {
        val throwable = RuntimeException("error")
        val domainError = DomainError.TimeoutError(null, "mapped")
        coEvery { apiService.getRecipesDetails("123") } throws throwable
        coEvery { errorMapper.mapError(throwable) } returns domainError

        val result = dataSource.getRecipesDetailsResponse("123")

        assertTrue(result is DomainResult.Error)
        assertEquals(domainError, (result as DomainResult.Error).value)
    }
}
