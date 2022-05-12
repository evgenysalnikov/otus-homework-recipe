package com.salnikoff.recipe.common.models

@JvmInline
value class RecipeRequirement(private val requirement: String) {
    fun asString() = requirement

    companion object {
        val NONE = RecipeRequirement("")
    }
}
