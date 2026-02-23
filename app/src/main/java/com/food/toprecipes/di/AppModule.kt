package com.food.toprecipes.di

import com.food.toprecipes.preferences.ThemePreferences
import com.food.toprecipes.preferences.ThemePreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindThemePreferences(impl: ThemePreferencesImpl): ThemePreferences
}
