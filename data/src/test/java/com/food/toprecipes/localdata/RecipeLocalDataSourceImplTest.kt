package com.food.toprecipes.localdata

import com.food.toprecipes.model.RecipeDetail
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RecipeLocalDataSourceImplTest {

    private lateinit var dao: RecipeDetailDao
    private lateinit var dataSource: RecipeLocalDataSourceImpl

    @Before
    fun setup() {
        dao = mockk()
        dataSource = RecipeLocalDataSourceImpl(dao)
    }

    @Test
    fun `should return null when getRecipeDetail and dao returns null`() = runTest {
        coEvery { dao.getById("123") } returns null

        val result = dataSource.getRecipeDetail("123")

        assertNull(result)
    }

    @Test
    fun `should return RecipeDetail when getRecipeDetail and dao returns entity`() = runTest {
        val entity = RecipeDetailEntity(
            recipeDetailId = "123",
            title = "Pizza",
            image = "img.png",
            sourceUrl = "https://example.com",
            instructions = null,
            ingredients = emptyList(),
            isFavorite = true
        )
        coEvery { dao.getById("123") } returns entity

        val result = dataSource.getRecipeDetail("123")

        assertEquals("123", result?.recipeDetailId)
        assertEquals("Pizza", result?.title)
        assertEquals(true, result?.isFavorite)
    }

    @Test
    fun `should return favorites flow from dao getFavorites`() = runTest {
        val entity = RecipeDetailEntity(
            recipeDetailId = "1",
            title = "Pasta",
            image = "",
            sourceUrl = "",
            instructions = null,
            ingredients = emptyList(),
            isFavorite = true
        )
        every { dao.getFavorites() } returns flowOf(listOf(entity))

        val flow = dataSource.getFavoriteRecipes()
        val list = flow.first()

        assertEquals(1, list.size)
        assertEquals("1", list[0].recipeDetailId)
        assertEquals("Pasta", list[0].title)
        assertEquals(true, list[0].isFavorite)
    }

    @Test
    fun `should insert entity when saveRecipeDetail and no existing`() = runTest {
        coEvery { dao.getById("123") } returns null
        coEvery { dao.insert(any()) } returns Unit

        val detail = RecipeDetail(
            recipeDetailId = "123",
            title = "Pizza",
            image = "",
            sourceUrl = "",
            instructions = null,
            ingredients = emptyList(),
            isFavorite = false
        )
        dataSource.saveRecipeDetail(detail, isFavorite = false)

        val slot = slot<RecipeDetailEntity>()
        coVerify(exactly = 1) { dao.insert(capture(slot)) }
        assertEquals("123", slot.captured.recipeDetailId)
        assertEquals(false, slot.captured.isFavorite)
    }

    @Test
    fun `should preserve isFavorite when saveRecipeDetail and existing has isFavorite true`() = runTest {
        val existing = RecipeDetailEntity(
            recipeDetailId = "123",
            title = "Pizza",
            image = "",
            sourceUrl = "",
            instructions = null,
            ingredients = emptyList(),
            isFavorite = true
        )
        coEvery { dao.getById("123") } returns existing
        coEvery { dao.insert(any()) } returns Unit

        val detail = RecipeDetail(
            recipeDetailId = "123",
            title = "Pizza",
            image = "",
            sourceUrl = "",
            instructions = null,
            ingredients = emptyList(),
            isFavorite = false
        )
        dataSource.saveRecipeDetail(detail, isFavorite = false)

        val slot = slot<RecipeDetailEntity>()
        coVerify(exactly = 1) { dao.insert(capture(slot)) }
        assertEquals(true, slot.captured.isFavorite)
    }

    @Test
    fun `should call dao setFavorite when setFavorite is called`() = runTest {
        coEvery { dao.setFavorite("123", true) } returns Unit

        dataSource.setFavorite("123", true)

        coVerify(exactly = 1) { dao.setFavorite("123", true) }
    }
}
