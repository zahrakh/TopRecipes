package com.food.toprecipes.presentation.composable.recipes

import ErrorView
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleLeft
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.food.toprecipes.presentation.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.food.toprecipes.model.RecipeDetail
import com.food.toprecipes.presentation.composable.LoadingView
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    recipeId: Int,
    onBackClick: () -> Unit,
    viewModel: RecipeDetailsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    //loadData
    LaunchedEffect(recipeId) {
        viewModel.loadRecipeDetails(recipeId.toString())
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { uiEffect ->
            when (uiEffect) {
                is RecipeDetailsUiEffect.ShowError -> {
                    //todo show Error dialog
                }

                is RecipeDetailsUiEffect.OpenWebSource -> {
                    launchCustomTab(context, uiEffect.url)
                }
            }
        }
    }

    RecipeDetailsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = { viewModel.loadRecipeDetails(recipeId.toString()) },
        onSourceClick = { url -> viewModel.onSourceUrlClicked(url) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsContent(
    modifier: Modifier = Modifier,
    uiState: RecipeDetailsUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onSourceClick: (url: String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.recipeDetail?.title ?: stringResource(R.string.recipe_details_screen_topbar_title_fallback)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowCircleLeft, contentDescription = stringResource(R.string.recipe_details_screen_nav_back_content_description))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            when {
                uiState.isLoading -> {
                    LoadingView(modifier)
                }

                uiState.errorMessageResId != null -> {
                    ErrorView(
                        message = stringResource(uiState.errorMessageResId!!),
                        onRetry = onRetry
                    )
                }

                uiState.recipeDetail != null -> {
                    RecipeDetailList(modifier = modifier, uiState.recipeDetail, onSourceClick)
                }
            }
        }
    }
}

@Composable
fun RecipeDetailList(modifier: Modifier, recipeDetail: RecipeDetail, onSourceClick: (String) -> Unit) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        item {
            AsyncImage(
                model = recipeDetail.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        }
        item {
            RecipeHeaderSection(recipeDetail)
        }
        items(recipeDetail.ingredients) { ingredient ->
            IngredientItem(ingredient.original)
        }
        item {
            InstructionSection(
                instructions = recipeDetail.instructions,
                onSourceClick = { onSourceClick(recipeDetail.sourceUrl) }
            )
        }

    }
}


@Composable
fun RecipeHeaderSection(
    recipe: RecipeDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = recipe.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.recipe_details_screen_ingredients_section_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}



@Composable
fun IngredientItem(
    ingredient: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = ingredient,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InstructionSection(
    instructions: String?,
    onSourceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.recipe_details_screen_instructions_section_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Allows users to long-press and copy text!
        SelectionContainer {
            Text(
                text = instructions ?: stringResource(R.string.recipe_details_screen_instructions_empty_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onSourceClick,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.recipe_details_screen_button_view_full_recipe_source))
        }
    }
}


private fun launchCustomTab(context: Context, url: String) {
    val uri = url.toUri()
    try {
        val intent = CustomTabsIntent.Builder()
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .setShowTitle(true)
            .build()

        intent.launchUrl(context, uri)
    } catch (e: Exception) {
        //todo should avoid general exception
        Log.d("InstructionSection", e.message ?: context.getString(R.string.error_generic_load_web_fail))
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(browserIntent)
        } catch (finalException: Exception) {
            Log.d("InstructionSection", finalException.message ?: context.getString(R.string.error_generic_load_web_fail))
            Toast.makeText(context, context.getString(R.string.recipe_details_screen_toast_no_browser_found), Toast.LENGTH_LONG).show()
        }
    }
}





