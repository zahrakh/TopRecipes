package com.food.toprecipes.remotedata.mapper

import com.food.toprecipes.model.Recipe
import com.food.toprecipes.model.RecipesResponse
import com.food.toprecipes.remotedata.IngredientResponseDTO
import com.food.toprecipes.remotedata.RecipeDetailsResponseDTO
import com.food.toprecipes.remotedata.RecipesDTO
import com.food.toprecipes.remotedata.RecipesResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecipeMapperTest {

    @Test
    fun `should return RecipesResponse when RecipesResponseDTO toDomain is called`() {
        val dto = RecipesResponseDTO(
            results = listOf(
                RecipesDTO(id = 1, title = "Pasta", image = "url", imageType = "jpg")
            ),
            offset = 0,
            number = 10,
            totalResults = 100
        )
        val result = dto.toDomain()
        assertEquals(1, result.recipes.size)
        assertEquals(0, result.offset)
        assertEquals(10, result.number)
        assertEquals(100, result.totalResults)
        assertEquals(1, result.recipes[0].id)
        assertEquals("Pasta", result.recipes[0].title)
    }

    @Test
    fun `should filter out null recipes when RecipesDTO has null id or title`() {
        val dto = RecipesResponseDTO(
            results = listOf(
                RecipesDTO(id = 1, title = "Valid", image = null, imageType = null),
                RecipesDTO(id = null, title = "No Id", image = null, imageType = null),
                RecipesDTO(id = 2, title = null, image = null, imageType = null)
            ),
            offset = 0,
            number = 3,
            totalResults = 3
        )
        val result = dto.toDomain()
        assertEquals(1, result.recipes.size)
        assertEquals(1, result.recipes[0].id)
        assertEquals("Valid", result.recipes[0].title)
    }

    @Test
    fun `should return Recipe when RecipesDTO has id and title`() {
        val dto = RecipesDTO(id = 42, title = "Soup", image = "img.png", imageType = "png")
        val result = dto.toDomain()
        assertEquals(Recipe(id = 42, title = "Soup", image = "img.png", imageType = "png"), result)
    }

    @Test
    fun `should return null when RecipesDTO has null id`() {
        val dto = RecipesDTO(id = null, title = "No Id", image = null, imageType = null)
        val result = dto.toDomain()
        assertNull(result)
    }

    @Test
    fun `should return null when RecipesDTO has null title`() {
        val dto = RecipesDTO(id = 1, title = null, image = null, imageType = null)
        val result = dto.toDomain()
        assertNull(result)
    }

    @Test
    fun `should return RecipeDetail when RecipeDetailsResponseDTO toDomain is called`() {
        val dto = RecipeDetailsResponseDTO(
            title = "Pizza",
            image = "pizza.png",
            sourceUrl = "https://example.com",
            instructions = "Bake it",
            extendedIngredients = listOf(IngredientResponseDTO(original = "Flour"))
        )
        val result = dto.toDomain("123")
        assertEquals("123", result.recipeDetailId)
        assertEquals("Pizza", result.title)
        assertEquals("pizza.png", result.image)
        assertEquals("https://example.com", result.sourceUrl)
        assertEquals("Bake it", result.instructions)
        assertEquals(1, result.ingredients.size)
        assertEquals("Flour", result.ingredients[0].original)
    }

    @Test
    fun `should return Ingredient when IngredientResponseDTO toDomain is called`() {
        val dto = IngredientResponseDTO(original = "Salt and pepper")
        val result = dto.toDomain()
        assertEquals("Salt and pepper", result.original)
    }
}
