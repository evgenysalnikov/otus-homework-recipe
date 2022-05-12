package com.salnikoff.recipe.common.models

@JvmInline
value class RecipeId(private val id: String) {
    fun asString() = id

    companion object {
        val NONE = RecipeId("")
    }
}
