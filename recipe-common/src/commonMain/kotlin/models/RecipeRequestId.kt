package com.salnikoff.recipe.common.models

@JvmInline
value class RecipeRequestId(private val id: String) {
    fun asString() = id

    companion object {
        val NONE = RecipeRequestId("")
    }
}
