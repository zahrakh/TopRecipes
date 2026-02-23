package com.food.toprecipes.presentation.composable.recipes

import androidx.lifecycle.viewModelScope
import com.food.toprecipes.data.DomainResult
import com.food.toprecipes.model.Recipe
import com.food.toprecipes.presentation.base.BaseViewModel
import com.food.toprecipes.presentation.mapper.DomainErrorToMessageMapper
import com.food.toprecipes.usecase.GetRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipesListUiState(
    val recipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    // Next offset to request (current size of loaded recipes)
    val nextOffset: Int = 0,
    // Total results from API
    val totalResults: Int? = null,
    // String resource ID for error message
    val errorMessageResId: Int? = null
) {
    //True when there are more items to load.
    val hasMore: Boolean
        get() = totalResults == null || nextOffset < totalResults
}

sealed class RecipesListUiEffect {
    data class ShowError(val messageResId: Int) : RecipesListUiEffect()
}

@HiltViewModel
class RecipesListViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase
) : BaseViewModel<RecipesListUiState, RecipesListUiEffect>() {

    override fun initialState() = RecipesListUiState()

    init {
        loadRecipes(reset = true)
    }


    // Loads recipes from the API.
    // parameter reset If true, loads from offset 0 and replaces the list.
    // If false, loads the next page and appends (pagination).

    fun loadRecipes(reset: Boolean = true) {
        if (reset) {
            if (uiState.value.isLoading) return
            setState { copy(isLoading = true, errorMessageResId = null) }
        } else {
            if (uiState.value.isLoadingMore || uiState.value.isLoading || !uiState.value.hasMore) return
            setState { copy(isLoadingMore = true) }
        }

        val offset = if (reset) 0 else uiState.value.nextOffset
        val query = uiState.value.searchQuery.trim()

        viewModelScope.launch {
            when (val result =
                getRecipesUseCase(offset = offset, number = PAGE_SIZE, query = query)) {
                is DomainResult.Success -> {
                    val resp = result.value
                    val nextOffset = (resp.offset ?: offset) + resp.recipes.size
                    if (reset) {
                        setState {
                            copy(
                                isLoading = false,
                                recipes = resp.recipes,
                                nextOffset = nextOffset,
                                totalResults = resp.totalResults
                            )
                        }
                    } else {
                        setState {
                            copy(
                                isLoadingMore = false,
                                recipes = recipes + resp.recipes,
                                nextOffset = nextOffset,
                                totalResults = resp.totalResults ?: totalResults
                            )
                        }
                    }
                }

                is DomainResult.Error -> {
                    val resId = DomainErrorToMessageMapper.toMessageResId(result.value)
                    setState {
                        copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessageResId = resId
                        )
                    }
                    setEffect(RecipesListUiEffect.ShowError(resId))
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        setState { copy(searchQuery = query) }
    }
}

private const val PAGE_SIZE = 10
