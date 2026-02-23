package com.food.toprecipes.preferences

import com.food.toprecipes.presentation.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Persists and exposes the user's theme preference (system/light/dark).
 * Used for the optional in-app theme toggle.
 */
interface ThemePreferences {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}
