package com.salnikoff.recipe.repo.inmemory.models

@JvmInline
value class RecipeRequirementEntity(private val requirement: String) {
    fun asString() = requirement

    companion object {
        val NONE = RecipeRequirementEntity("")
    }
}