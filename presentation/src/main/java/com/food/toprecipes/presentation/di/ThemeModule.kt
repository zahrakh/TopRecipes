package com.food.toprecipes.presentation.di

import com.food.toprecipes.presentation.theme.ThemePreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ThemeModule {

    @Binds
    @Singleton
    abstract fun bindThemePreferences(impl: ThemePreferencesImpl): ThemePreferences
}
