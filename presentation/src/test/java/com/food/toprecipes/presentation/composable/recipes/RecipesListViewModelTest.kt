package com.food.toprecipes.presentation.composable.recipes

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.Recipe
import com.food.toprecipes.model.RecipesResponse
import com.food.toprecipes.presentation.base.MainDispatcherRule
import com.food.toprecipes.usecase.GetRecipesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.advanceUntilIdle
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipesListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getRecipesUseCase: GetRecipesUseCase
    private lateinit var viewModel: RecipesListViewModel

    @Before
    fun setup() {
        getRecipesUseCase = mockk()
        // Stub so init {} loadRecipes() call succeeds and tests start from a known state
        coEvery { getRecipesUseCase(offset = 0, number = 10, query = "") } returns DomainResult.Success(
            RecipesResponse(recipes = emptyList(), offset = 0, number = 10, totalResults = 0)
        )
        viewModel = RecipesListViewModel(getRecipesUseCase)
    }

    @Test
    fun `should have initial state with empty recipes and no error`() {
        assertEquals(emptyList<Recipe>(), viewModel.uiState.value.recipes)
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.errorMessageResId)
    }

    @Test
    fun `should update recipes and clear loading when loadRecipes succeeds with reset`() = runTest {
        val response = RecipesResponse(
            recipes = listOf(Recipe(1, "Pasta", "url", "jpg")),
            offset = 0,
            number = 10,
            totalResults = 1
        )
        coEvery { getRecipesUseCase(offset = 0, number = 10, query = "") } returns DomainResult.Success(response)

        viewModel.loadRecipes(reset = true)

        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.recipes.size)
        assertEquals("Pasta", viewModel.uiState.value.recipes[0].title)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.nextOffset)
        assertEquals(1, viewModel.uiState.value.totalResults)
    }

    @Test
    fun `should append recipes when loadRecipes succeeds without reset`() = runTest {
        val first = RecipesResponse(
            recipes = listOf(Recipe(1, "Pasta", null, null)),
            offset = 0,
            number = 10,
            totalResults = 2
        )
        val second = RecipesResponse(
            recipes = listOf(Recipe(2, "Pizza", null, null)),
            offset = 10,
            number = 10,
            totalResults = 2
        )
        coEvery { getRecipesUseCase(offset = 0, number = 10, query = "") } returns DomainResult.Success(first)
        viewModel.loadRecipes(reset = true)
        advanceUntilIdle()

        coEvery { getRecipesUseCase(offset = 1, number = 10, query = "") } returns DomainResult.Success(second)
        viewModel.loadRecipes(reset = false)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.recipes.size)
        assertEquals("Pasta", viewModel.uiState.value.recipes[0].title)
        assertEquals("Pizza", viewModel.uiState.value.recipes[1].title)
        assertEquals(false, viewModel.uiState.value.isLoadingMore)
    }

    @Test
    fun `should set error state and effect when loadRecipes fails`() = runTest {
        val error = DomainError.ServiceUnavailable(null, "Unavailable")
        coEvery { getRecipesUseCase(offset = 0, number = 10, query = "") } returns DomainResult.Error(error)

        viewModel.loadRecipes(reset = true)
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(false, viewModel.uiState.value.isLoadingMore)
        coVerify(atLeast = 1) { getRecipesUseCase(offset = 0, number = 10, query = "") }
    }

    @Test
    fun `should update search query when updateSearchQuery is called`() {
        viewModel.updateSearchQuery("chicken")
        assertEquals("chicken", viewModel.uiState.value.searchQuery)
    }
}
