package com.food.toprecipes.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorView(modifier: Modifier) {
    Box(
        modifier = modifier
    ) {
        CircularProgressIndicator()
    }
}