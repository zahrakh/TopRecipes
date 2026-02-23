package com.food.toprecipes.repository

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.localdata.RecipeLocalDataSource
import com.food.toprecipes.model.Recipe
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.remotedata.RecipeDetailsResponseDTO
import com.food.toprecipes.remotedata.RecipesResponseDTO
import com.food.toprecipes.spoonacularapi.SpoonacularRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpoonacularRepositoryImplTest {

    private lateinit var remoteDataSource: SpoonacularRemoteDataSource
    private lateinit var localDataSource: RecipeLocalDataSource
    private lateinit var repository: SpoonacularRepositoryImpl

    @Before
    fun setup() {
        remoteDataSource = mockk()
        localDataSource = mockk()
        repository = SpoonacularRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `should return Success with mapped RecipesResponse when getRecipes succeeds`() = runTest {
        val dto = RecipesResponseDTO(
            results = listOf(
                com.food.toprecipes.remotedata.RecipesDTO(1, "Pasta", "url", "jpg")
            ),
            offset = 0,
            number = 10,
            totalResults = 1
        )
        coEvery {
            remoteDataSource.getRecipesResponse(offset = 0, number = 10, query = "")
        } returns DomainResult.Success(dto)

        val result = repository.getRecipes(offset = 0, number = 10, query = "")

        assertTrue(result is DomainResult.Success)
        val response = (result as DomainResult.Success).value
        assertEquals(1, response.recipes.size)
        assertEquals(Recipe(1, "Pasta", "url", "jpg"), response.recipes[0])
        assertEquals(0, response.offset)
        assertEquals(10, response.number)
        assertEquals(1, response.totalResults)
    }

    @Test
    fun `should return Error when getRecipes fails`() = runTest {
        val error = DomainError.ServiceUnavailable(null, "Unavailable")
        coEvery {
            remoteDataSource.getRecipesResponse(offset = 0, number = 10, query = "")
        } returns DomainResult.Error(error)

        val result = repository.getRecipes(offset = 0, number = 10, query = "")

        assertTrue(result is DomainResult.Error)
        assertEquals(error, (result as DomainResult.Error).value)
    }

    @Test
    fun `should return cached recipe when getRecipeDetails has local cache`() = runTest {
        val cached = RecipeDetail(
            recipeDetailId = "123",
            title = "Cached",
            image = "",
            sourceUrl = "",
            instructions = null,
            ingredients = emptyList(),
            isFavorite = true
        )
        coEvery { localDataSource.getRecipeDetail("123") } returns cached

        val result = repository.getRecipeDetails("123")

        assertTrue(result is DomainResult.Success)
        assertEquals(cached, (result as DomainResult.Success).value)
        coVerify(exactly = 0) { remoteDataSource.getRecipesDetailsResponse(any()) }
    }

    @Test
    fun `should fetch from remote and save when getRecipeDetails has no cache`() = runTest {
        coEvery { localDataSource.getRecipeDetail("123") } returns null
        val dto = RecipeDetailsResponseDTO(
            title = "Pizza",
            image = "img.png",
            sourceUrl = "https://example.com",
            instructions = null,
            extendedIngredients = emptyList()
        )
        coEvery { remoteDataSource.getRecipesDetailsResponse("123") } returns DomainResult.Success(dto)
        coEvery { localDataSource.getRecipeDetail("123") } returns null
        coEvery { localDataSource.saveRecipeDetail(any(), any()) } returns Unit

        val result = repository.getRecipeDetails("123")

        assertTrue(result is DomainResult.Success)
        val detail = (result as DomainResult.Success).value
        assertEquals("123", detail.recipeDetailId)
        assertEquals("Pizza", detail.title)
        assertEquals(false, detail.isFavorite)
        coVerify(exactly = 1) { localDataSource.saveRecipeDetail(any(), any()) }
    }

    @Test
    fun `should return Error when getRecipeDetails has no cache and remote fails`() = runTest {
        coEvery { localDataSource.getRecipeDetail("123") } returns null
        val error = DomainError.TimeoutError(null, "Timeout")
        coEvery { remoteDataSource.getRecipesDetailsResponse("123") } returns DomainResult.Error(error)

        val result = repository.getRecipeDetails("123")

        assertTrue(result is DomainResult.Error)
        assertEquals(error, (result as DomainResult.Error).value)
    }

    @Test
    fun `should delegate setFavorite to localDataSource`() = runTest {
        coEvery { localDataSource.setFavorite("123", true) } returns Unit

        repository.setFavorite("123", true)

        coVerify(exactly = 1) { localDataSource.setFavorite("123", true) }
    }

    @Test
    fun `should return getFavoriteRecipes flow from localDataSource`() = runTest {
        val favorites = listOf(
            RecipeDetail("1", "Pasta", "", "", null, emptyList(), true)
        )
        coEvery { localDataSource.getFavoriteRecipes() } returns flowOf(favorites)

        val list = repository.getFavoriteRecipes().first()

        assertEquals(favorites, list)
    }
}
