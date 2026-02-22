package com.food.toprecipes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.food.toprecipes.presentation.composable.recipes.RecipeDetailsScreen
import com.food.toprecipes.presentation.composable.recipes.RecipesListScreen
import kotlinx.serialization.Serializable

sealed interface RecipesRoute {
    @Serializable
    data object RecipesList : RecipesRoute

    @Serializable
    data class Details(val recipeId: Int) : RecipesRoute
}

@Composable
fun RecipesNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = RecipesRoute.RecipesList
    ) {
        composable<RecipesRoute.RecipesList> {
            RecipesListScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(RecipesRoute.Details(recipeId))
                }
            )
        }
        composable<RecipesRoute.Details> { backStackEntry ->
            val details: RecipesRoute.Details = backStackEntry.toRoute()//todo add test
            RecipeDetailsScreen(
                recipeId = details.recipeId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
