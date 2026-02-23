package com.food.toprecipes.usecase

import com.food.toprecipes.repository.SpoonacularRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private lateinit var repository: SpoonacularRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun `should call setFavorite with true when invoke is called with isFavorite true`() = runTest {
        useCase("42", true)

        coVerify(exactly = 1) { repository.setFavorite("42", true) }
    }

    @Test
    fun `should call setFavorite with false when invoke is called with isFavorite false`() = runTest {
        useCase("99", false)

        coVerify(exactly = 1) { repository.setFavorite("99", false) }
    }
}
