package com.salnikoff.recipe.common.models

@JvmInline
value class UserId(private val recipeUserId: String) {
    fun asString() = recipeUserId

    companion object {
        val NONE = UserId("")
    }
}
