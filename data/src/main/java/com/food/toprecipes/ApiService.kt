package com.food.toprecipes

import com.food.toprecipes.remotedata.RecipeDetailsResponseDTO
import com.food.toprecipes.remotedata.RecipesResponseDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    companion object {
        const val BASE_URL = "https://api.spoonacular.com/"
    }

    @GET("recipes/complexSearch")
    suspend fun getRecipes(
    ): RecipesResponseDTO

    @GET("https://api.spoonacular.com/recipes/{recipeId}/information")
    suspend fun getRecipesDetails(
        @Path("recipeId") recipeId: String
    ): RecipeDetailsResponseDTO


}
