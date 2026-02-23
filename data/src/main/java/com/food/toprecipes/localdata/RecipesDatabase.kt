package com.food.toprecipes.localdata

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RecipeDetailEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RecipesDatabase : RoomDatabase() {
    abstract fun recipeDetailDao(): RecipeDetailDao
}
