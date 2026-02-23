package com.food.toprecipes.localdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecipeDetailEntity)

    @Query("SELECT * FROM recipe_cache WHERE recipeDetailId = :recipeId")
    suspend fun getById(recipeId: String): RecipeDetailEntity?

    @Query("SELECT * FROM recipe_cache WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<RecipeDetailEntity>>

    @Query("UPDATE recipe_cache SET isFavorite = :isFavorite WHERE recipeDetailId = :recipeId")
    suspend fun setFavorite(recipeId: String, isFavorite: Boolean)
}
