package com.food.toprecipes.usecase

import com.food.toprecipes.model.Ingredient
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.repository.SpoonacularRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetFavoriteRecipesUseCaseTest {

    private lateinit var repository: SpoonacularRepository
    private lateinit var useCase: GetFavoriteRecipesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetFavoriteRecipesUseCase(repository)
    }

    @Test
    fun `should return favorites flow when repository returns favorites`() = runTest {
        val favorites = listOf(
            RecipeDetail(
                recipeDetailId = "1",
                title = "Pasta",
                image = "url",
                sourceUrl = "https://a.com",
                instructions = null,
                ingredients = listOf(Ingredient("Flour")),
                isFavorite = true
            )
        )
        every { repository.getFavoriteRecipes() } returns flowOf(favorites)

        val result = useCase().toList()

        assertEquals(1, result.size)
        assertEquals(favorites, result.first())
        verify(exactly = 1) { repository.getFavoriteRecipes() }
    }

    @Test
    fun `should return empty list when repository has no favorites`() = runTest {
        every { repository.getFavoriteRecipes() } returns flowOf(emptyList())

        val result = useCase().toList()

        assertEquals(listOf(emptyList<RecipeDetail>()), result)
    }
}
