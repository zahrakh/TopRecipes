package com.food.toprecipes.presentation.composable.recipes

import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.presentation.base.MainDispatcherRule
import com.food.toprecipes.usecase.GetRecipesDetailsUseCase
import com.food.toprecipes.usecase.ToggleFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getRecipesDetailsUseCase: GetRecipesDetailsUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var viewModel: RecipeDetailsViewModel

    private val recipeDetail = RecipeDetail(
        recipeDetailId = "123",
        title = "Pizza",
        image = "img.png",
        sourceUrl = "https://example.com",
        instructions = "Bake",
        ingredients = emptyList(),
        isFavorite = false
    )

    @Before
    fun setup() {
        getRecipesDetailsUseCase = mockk()
        toggleFavoriteUseCase = mockk()
        viewModel = RecipeDetailsViewModel(getRecipesDetailsUseCase, toggleFavoriteUseCase)
    }

    @Test
    fun `should have initial state with null recipe and not loading`() {
        assertNull(viewModel.uiState.value.recipeDetail)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `should set recipe detail and clear loading when loadRecipeDetails succeeds`() = runTest {
        coEvery { getRecipesDetailsUseCase.invoke("123") } returns DomainResult.Success(recipeDetail)

        viewModel.loadRecipeDetails("123")
        advanceUntilIdle()

        assertEquals(recipeDetail, viewModel.uiState.value.recipeDetail)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `should set error state when loadRecipeDetails fails`() = runTest {
        val error = DomainError.TimeoutError(null, "Timeout")
        coEvery { getRecipesDetailsUseCase.invoke("123") } returns DomainResult.Error(error)

        viewModel.loadRecipeDetails("123")
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(true, viewModel.uiState.value.errorMessageResId != null)
    }

    @Test
    fun `should not load when already loading`() = runTest {
        coEvery { getRecipesDetailsUseCase.invoke(any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            DomainResult.Success(recipeDetail)
        }

        viewModel.loadRecipeDetails("123")
        viewModel.loadRecipeDetails("123")

        coVerify(exactly = 1) { getRecipesDetailsUseCase.invoke("123") }
    }

    @Test
    fun `should not load when same recipe already loaded`() = runTest {
        coEvery { getRecipesDetailsUseCase.invoke("123") } returns DomainResult.Success(recipeDetail)

        viewModel.loadRecipeDetails("123")
        advanceUntilIdle()
        viewModel.loadRecipeDetails("123")

        coVerify(exactly = 1) { getRecipesDetailsUseCase.invoke("123") }
    }

    @Test
    fun `should emit OpenWebSource effect when onSourceUrlClicked with https url`() = runTest {
        coEvery { getRecipesDetailsUseCase.invoke("123") } returns DomainResult.Success(recipeDetail)
        viewModel.loadRecipeDetails("123")
        advanceUntilIdle()

        val effects = mutableListOf<RecipeDetailsUiEffect>()
        val job = launch { viewModel.uiEffect.collect { effects.add(it) } }
        advanceUntilIdle() // let collector subscribe before we emit
        viewModel.onSourceUrlClicked("https://safe.com")
        advanceUntilIdle()

        assertEquals(RecipeDetailsUiEffect.OpenWebSource("https://safe.com"), effects.singleOrNull())
        job.cancel()
    }

    @Test
    fun `should not emit effect when onSourceUrlClicked with null or blank url`() = runTest {
        val effects = mutableListOf<RecipeDetailsUiEffect>()
        val job = launch { viewModel.uiEffect.collect { effects.add(it) } }

        viewModel.onSourceUrlClicked(null)
        viewModel.onSourceUrlClicked("")

        assertEquals(0, effects.size)
        job.cancel()
    }

    @Test
    fun `should call toggleFavoriteUseCase and update state when toggleFavorite is called`() = runTest {
        coEvery { getRecipesDetailsUseCase.invoke("123") } returns DomainResult.Success(recipeDetail)
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel.loadRecipeDetails("123")
        advanceUntilIdle()

        viewModel.toggleFavorite("123")
        advanceUntilIdle()

        assertEquals(true, viewModel.uiState.value.recipeDetail?.isFavorite)
        coVerify(exactly = 1) { toggleFavoriteUseCase("123", true) }
    }

    @Test
    fun `should not toggle when recipe id does not match`() = runTest {
        coEvery { getRecipesDetailsUseCase.invoke("123") } returns DomainResult.Success(recipeDetail)
        viewModel.loadRecipeDetails("123")
        advanceUntilIdle()

        viewModel.toggleFavorite("456")

        coVerify(exactly = 0) { toggleFavoriteUseCase(any(), any()) }
    }
}
