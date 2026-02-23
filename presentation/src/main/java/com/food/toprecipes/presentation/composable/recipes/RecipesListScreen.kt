package com.food.toprecipes.presentation.composable.recipes

import ErrorView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.food.toprecipes.presentation.R
import com.food.toprecipes.presentation.theme.ThemeMode
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.food.toprecipes.model.Recipe
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesListScreen(
    onRecipeClick: (Int) -> Unit,
    onThemeModeChange: ((ThemeMode) -> Unit)? = null,
    viewModel: RecipesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uiEffect by viewModel.uiEffect.collectAsState(initial = null)

    LaunchedEffect(uiEffect) {
        when (val effect = uiEffect) {
            is RecipesListUiEffect.ShowError -> {
                // Handle error loading if needed
            }

            null -> {}
        }
    }

    RecipesListContent(
        uiState = uiState,
        onRecipeClick = onRecipeClick,
        onRetry = viewModel::loadRecipes,
        onLoadMore = { viewModel.loadRecipes(reset = false) },
        onThemeModeChange = onThemeModeChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipesListContent(
    uiState: RecipesListUiState,
    onRecipeClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onThemeModeChange: ((ThemeMode) -> Unit)? = null
) {
    var themeMenuExpanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recipes_list_screen_topbar_title)) },
                actions = {
                    if (onThemeModeChange != null) {
                        Box {
                            IconButton(
                                onClick = { themeMenuExpanded = true },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.SettingsBrightness,
                                        contentDescription = stringResource(R.string.theme_toggle_content_description)
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = themeMenuExpanded,
                                onDismissRequest = { themeMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.theme_mode_system)) },
                                    onClick = {
                                        onThemeModeChange(ThemeMode.SYSTEM)
                                        themeMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.theme_mode_light)) },
                                    onClick = {
                                        onThemeModeChange(ThemeMode.LIGHT)
                                        themeMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.theme_mode_dark)) },
                                    onClick = {
                                        onThemeModeChange(ThemeMode.DARK)
                                        themeMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.recipes.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.errorMessageResId != null && uiState.recipes.isEmpty() -> {
                    ErrorView(
                        message = stringResource(uiState.errorMessageResId),
                        onRetry = onRetry
                    )
                }

                else -> {
                    RecipeList(
                        recipes = uiState.recipes,
                        onRecipeClick = onRecipeClick,
                        hasMore = uiState.hasMore,
                        isLoadingMore = uiState.isLoadingMore,
                        onLoadMore = onLoadMore
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeList(
    recipes: List<Recipe>,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    hasMore: Boolean = false,
    isLoadingMore: Boolean = false,
    onLoadMore: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recipes.size, key = { recipes[it].id }) { index ->
            RecipeItem(
                recipe = recipes[index],
                onClick = { onRecipeClick(recipes[index].id) }
            )
        }
        if (hasMore) {
            item(key = "load_more") {
                LaunchedEffect(Unit) {
                    onLoadMore()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoadingMore) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeItem(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(recipe.image)
                    .crossfade(true)
                    .build(),
                contentDescription = recipe.title,
                placeholder = painterResource(R.drawable.ic_placeholder_recipe),
                error = painterResource(R.drawable.ic_error_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.small)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.title?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.recipes_list_item_title_untitled_fallback),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                recipe.id.let { //todo check to see if I must show Id or not? and if Id can be null or not!
                    Text(
                        text = stringResource(R.string.recipes_list_item_id_format, recipe.id),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}