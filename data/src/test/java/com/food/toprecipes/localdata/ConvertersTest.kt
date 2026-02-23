package com.food.toprecipes.localdata

import com.food.toprecipes.model.Ingredient
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `should return delimiter-separated string when ingredientsToJson is called`() {
        val ingredients = listOf(
            Ingredient(original = "Salt"),
            Ingredient(original = "Pepper")
        )
        val result = converters.ingredientsToJson(ingredients)
        assertEquals("Salt\u0001Pepper", result)
    }

    @Test
    fun `should return empty string when ingredientsToJson is called with empty list`() {
        val result = converters.ingredientsToJson(emptyList())
        assertEquals("", result)
    }

    @Test
    fun `should return list of Ingredient when jsonToIngredients is called`() {
        val value = "Flour\u0001Water\u0001Salt"
        val result = converters.jsonToIngredients(value)
        assertEquals(3, result.size)
        assertEquals("Flour", result[0].original)
        assertEquals("Water", result[1].original)
        assertEquals("Salt", result[2].original)
    }

    @Test
    fun `should return empty list when jsonToIngredients is called with blank string`() {
        val result = converters.jsonToIngredients("")
        assertEquals(0, result.size)
        val resultWhitespace = converters.jsonToIngredients("   ")
        assertEquals(0, resultWhitespace.size)
    }

    @Test
    fun `should round-trip ingredients through jsonToIngredients and ingredientsToJson`() {
        val ingredients = listOf(
            Ingredient(original = "One"),
            Ingredient(original = "Two")
        )
        val json = converters.ingredientsToJson(ingredients)
        val back = converters.jsonToIngredients(json)
        assertEquals(ingredients, back)
    }
}
