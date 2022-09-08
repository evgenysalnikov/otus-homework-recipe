package com.salnikoff.recipe.common.repo

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.common.models.RecipeError

data class DbRecipesResponse(
    override val result: List<Recipe>?,
    override val isSuccess: Boolean,
    override val errors: List<RecipeError> = emptyList()
) : IDbResponse<List<Recipe>>
