package com.food.toprecipes.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.food.toprecipes.presentation.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

class ThemePreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemePreferences {

    override val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[THEME_MODE_KEY] ?: THEME_MODE_SYSTEM) {
            THEME_MODE_LIGHT -> ThemeMode.LIGHT
            THEME_MODE_DARK -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = when (mode) {
                ThemeMode.SYSTEM -> THEME_MODE_SYSTEM
                ThemeMode.LIGHT -> THEME_MODE_LIGHT
                ThemeMode.DARK -> THEME_MODE_DARK
            }
        }
    }

    companion object {
        private const val THEME_MODE_SYSTEM = "system"
        private const val THEME_MODE_LIGHT = "light"
        private const val THEME_MODE_DARK = "dark"
    }
}
