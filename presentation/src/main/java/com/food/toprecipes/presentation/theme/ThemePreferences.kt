package com.food.toprecipes.presentation.theme

import kotlinx.coroutines.flow.Flow

interface ThemePreferences {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
