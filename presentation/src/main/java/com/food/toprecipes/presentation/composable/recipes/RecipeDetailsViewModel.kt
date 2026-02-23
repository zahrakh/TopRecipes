package com.food.toprecipes.presentation.composable.recipes

import androidx.lifecycle.viewModelScope
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.presentation.base.BaseViewModel
import com.food.toprecipes.presentation.mapper.DomainErrorToMessageMapper
import com.food.toprecipes.presentation.R
import com.food.toprecipes.usecase.GetRecipesDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDetailsUiState(
    val isLoading: Boolean = false,
    val recipeDetail: RecipeDetail? = null,
    val errorMessageResId: Int? = null
)

sealed class RecipeDetailsUiEffect {
    data class ShowError(val messageResId: Int) : RecipeDetailsUiEffect()
    data class OpenWebSource(val url: String) : RecipeDetailsUiEffect()
}

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val getRecipesDetailsUseCase: GetRecipesDetailsUseCase
) : BaseViewModel<RecipeDetailsUiState, RecipeDetailsUiEffect>() {

    override fun initialState() = RecipeDetailsUiState()

    fun loadRecipeDetails(recipeId: String) {

        if (uiState.value.isLoading || uiState.value.recipeDetail?.recipeDetailId == recipeId) return
        setState { copy(isLoading = true, errorMessageResId = null) }

        viewModelScope.launch {
            when (val result = getRecipesDetailsUseCase.invoke(recipeId)) {
                is DomainResult.Success -> {
                    setState { copy(isLoading = false, recipeDetail = result.value) }
                }

                is DomainResult.Error -> {
                    val resId = DomainErrorToMessageMapper.toMessageResId(result.value)
                    setState { copy(isLoading = false, errorMessageResId = resId) }
                    setEffect(RecipeDetailsUiEffect.ShowError(resId))
                }
            }
        }
    }

    fun onSourceUrlClicked(url: String?) {
        if (url.isNullOrBlank()) return
        if (url.startsWith("https://", ignoreCase = true)) {
            setEffect(RecipeDetailsUiEffect.OpenWebSource(url))
        } else {
            setState { copy(errorMessageResId = R.string.error_unsafe_link_blocked) }
            setEffect(RecipeDetailsUiEffect.ShowError(R.string.error_unsafe_link_blocked))
        }
    }
}
