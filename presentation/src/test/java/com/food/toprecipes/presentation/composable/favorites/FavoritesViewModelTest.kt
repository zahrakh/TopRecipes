@file:OptIn(ExperimentalCoroutinesApi::class)

package com.food.toprecipes.presentation.composable.favorites

import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.presentation.base.MainDispatcherRule
import com.food.toprecipes.usecase.GetFavoriteRecipesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        getFavoriteRecipesUseCase = mockk()
    }

    @Test
    fun `should have initial empty favorites state`() {
        every { getFavoriteRecipesUseCase() } returns flowOf(emptyList())
        viewModel = FavoritesViewModel(getFavoriteRecipesUseCase)

        assertEquals(FavoritesUiState(), viewModel.uiState.value)
    }

    @Test
    fun `should emit favorites from use case flow`() = runTest {
        val favorites = listOf(
            RecipeDetail(
                recipeDetailId = "1",
                title = "Pasta",
                image = "",
                sourceUrl = "",
                instructions = null,
                ingredients = emptyList(),
                isFavorite = true
            )
        )
        every { getFavoriteRecipesUseCase() } returns flowOf(favorites)
        viewModel = FavoritesViewModel(getFavoriteRecipesUseCase)

        // Subscribe so stateIn(WhileSubscribed) actually collects the use case flow
        val collectJob = launch { viewModel.favorites.collect { } }
        advanceUntilIdle()

        assertEquals(favorites, viewModel.favorites.value)
        collectJob.cancel()
    }
}
