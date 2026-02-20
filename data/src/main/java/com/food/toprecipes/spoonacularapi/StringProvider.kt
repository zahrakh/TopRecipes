package com.food.toprecipes.spoonacularapi

import androidx.annotation.StringRes

interface StringProvider {
    fun getString(@StringRes id: Int): String
}