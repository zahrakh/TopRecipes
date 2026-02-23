package com.food.toprecipes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.food.toprecipes.presentation.composable.favorites.FavoritesScreen
import com.food.toprecipes.presentation.composable.recipes.RecipeDetailsScreen
import com.food.toprecipes.presentation.composable.recipes.RecipesListScreen
import com.food.toprecipes.presentation.theme.ThemeMode
import kotlinx.serialization.Serializable

sealed interface RecipesRoute {
    @Serializable
    data object RecipesList : RecipesRoute

    @Serializable
    data object Favorites : RecipesRoute

    @Serializable
    data class Details(val recipeId: Int) : RecipesRoute
}

@Composable
fun RecipesNavHost(
    navController: NavHostController,
    onThemeModeChange: ((ThemeMode) -> Unit)? = null
) {
    NavHost(
        navController = navController,
        startDestination = RecipesRoute.RecipesList
    ) {
        composable<RecipesRoute.RecipesList> {
            RecipesListScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(RecipesRoute.Details(recipeId))
                },
                onNavigateToFavorites = { navController.navigate(RecipesRoute.Favorites) },
                onThemeModeChange = onThemeModeChange
            )
        }
        composable<RecipesRoute.Favorites> {
            FavoritesScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(RecipesRoute.Details(recipeId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<RecipesRoute.Details> { backStackEntry ->
            val details: RecipesRoute.Details = backStackEntry.toRoute()
            RecipeDetailsScreen(
                recipeId = details.recipeId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
