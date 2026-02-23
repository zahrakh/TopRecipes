package com.food.toprecipes.presentation.composable.favorites

import androidx.lifecycle.viewModelScope
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.presentation.base.BaseViewModel
import com.food.toprecipes.usecase.GetFavoriteRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FavoritesUiState(
    val favorites: List<RecipeDetail> = emptyList()
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase
) : BaseViewModel<FavoritesUiState, Unit>() {

    override fun initialState(): FavoritesUiState = FavoritesUiState()

    val favorites: StateFlow<List<RecipeDetail>> = getFavoriteRecipesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
