package com.salnikoff.recipe.common.models

@JvmInline
value class RecipeUserId(private val recipeUserId: String) {
    fun asString() = recipeUserId

    companion object {
        val NONE = RecipeUserId("")
    }
}
