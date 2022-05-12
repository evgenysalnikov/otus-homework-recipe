package com.salnikoff.recipe.common.models

data class RecipeError(
    val code: String = "",
    val group: String = "",
    val field: String = "",
    val message: String = "",
    val exception: Throwable? = null,
)
