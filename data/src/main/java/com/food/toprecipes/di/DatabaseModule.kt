package com.food.toprecipes.di

import android.content.Context
import androidx.room.Room
import com.food.toprecipes.localdata.RecipeDetailDao
import com.food.toprecipes.localdata.RecipeLocalDataSource
import com.food.toprecipes.localdata.RecipeLocalDataSourceImpl
import com.food.toprecipes.localdata.RecipesDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindRecipeLocalDataSource(impl: RecipeLocalDataSourceImpl): RecipeLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideRecipesDatabase(@ApplicationContext context: Context): RecipesDatabase =
            Room.databaseBuilder(
                context,
                RecipesDatabase::class.java,
                "recipes.db"
            ).build()

        @Provides
        @Singleton
        fun provideRecipeDetailDao(database: RecipesDatabase): RecipeDetailDao =
            database.recipeDetailDao()
    }
}
