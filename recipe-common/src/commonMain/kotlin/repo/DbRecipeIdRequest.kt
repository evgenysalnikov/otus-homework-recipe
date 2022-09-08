package com.salnikoff.recipe.common.repo

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.common.models.RecipeLock

data class DbRecipeIdRequest(
    val id: RecipeId,
    val lock: RecipeLock = RecipeLock.NONE
) {
    constructor(recipe: Recipe) : this(recipe.id, recipe.lock)
}
