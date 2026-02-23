package com.food.toprecipes.localdata.mapper

import com.food.toprecipes.localdata.RecipeDetailEntity
import com.food.toprecipes.model.Ingredient
import com.food.toprecipes.model.RecipeDetail
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeEntityMapperTest {

    @Test
    fun `should return RecipeDetail when RecipeDetailEntity toDomain is called`() {
        val entity = RecipeDetailEntity(
            recipeDetailId = "42",
            title = "Soup",
            image = "img.png",
            sourceUrl = "https://a.com",
            instructions = "Cook",
            ingredients = listOf(Ingredient("Water")),
            isFavorite = true
        )
        val result = entity.toDomain()
        assertEquals("42", result.recipeDetailId)
        assertEquals("Soup", result.title)
        assertEquals("img.png", result.image)
        assertEquals("https://a.com", result.sourceUrl)
        assertEquals("Cook", result.instructions)
        assertEquals(1, result.ingredients.size)
        assertEquals("Water", result.ingredients[0].original)
        assertEquals(true, result.isFavorite)
    }

    @Test
    fun `should return RecipeDetailEntity when RecipeDetail toEntity is called`() {
        val detail = RecipeDetail(
            recipeDetailId = "99",
            title = "Pasta",
            image = "pasta.png",
            sourceUrl = "https://b.com",
            instructions = null,
            ingredients = listOf(Ingredient("Flour"), Ingredient("Eggs")),
            isFavorite = false
        )
        val result = detail.toEntity(isFavorite = true)
        assertEquals("99", result.recipeDetailId)
        assertEquals("Pasta", result.title)
        assertEquals("pasta.png", result.image)
        assertEquals("https://b.com", result.sourceUrl)
        assertEquals(null, result.instructions)
        assertEquals(2, result.ingredients.size)
        assertEquals(true, result.isFavorite)
    }
}
