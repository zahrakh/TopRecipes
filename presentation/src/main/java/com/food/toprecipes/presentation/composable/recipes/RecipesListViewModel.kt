package com.food.toprecipes.presentation.composable.recipes

import androidx.lifecycle.viewModelScope
import com.food.toprecipes.data.DomainError
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.Recipe
import com.food.toprecipes.presentation.base.BaseViewModel
import com.food.toprecipes.presentation.mapper.DomainErrorToMessageMapper
import com.food.toprecipes.usecase.GetRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipesListUiState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    /** String resource ID for error message; null when no error. */
    val errorMessageResId: Int? = null
)

sealed class RecipesListUiEffect {
    data class ShowError(val messageResId: Int) : RecipesListUiEffect()
}

@HiltViewModel
class RecipesListViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase
) : BaseViewModel<RecipesListUiState, RecipesListUiEffect>() {

    override fun initialState() = RecipesListUiState()

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        if (uiState.value.isLoading) return

        setState { copy(isLoading = true, errorMessageResId = null) }

        viewModelScope.launch {
            when (val result = getRecipesUseCase()) {
                is DomainResult.Success -> {
                    setState { copy(isLoading = false, recipes = result.value.recipes) }
                }
                is DomainResult.Error -> {
                    val resId = DomainErrorToMessageMapper.toMessageResId(result.value)
                    setState { copy(isLoading = false, errorMessageResId = resId) }
                    setEffect(RecipesListUiEffect.ShowError(resId))
                }
            }
        }
    }
}
