package com.food.toprecipes.localdata

import androidx.room.TypeConverter
import com.food.toprecipes.model.Ingredient

private const val INGREDIENTS_DELIMITER = "\u0001"

class Converters {

    @TypeConverter
    fun ingredientsToJson(ingredients: List<Ingredient>): String =
        ingredients.joinToString(INGREDIENTS_DELIMITER) { it.original }

    @TypeConverter
    fun jsonToIngredients(value: String): List<Ingredient> {
        if (value.isBlank()) return emptyList()
        return value.split(INGREDIENTS_DELIMITER).map { Ingredient(original = it) }
    }
}
