package com.salnikoff.recipe.backend.services

import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeError

fun RecipeContext.errorResponse(buildError: () -> RecipeError, error: (RecipeError) -> RecipeError) = apply {
    state = CorState.FAILING
    errors.add(error(buildError()))
}

fun RecipeContext.successResponse(context: RecipeContext.() -> Unit) = apply(context)
    .apply { state = CorState.RUNNING }

val notFoundError: (String) -> String = { "Not found recipe by id $it" }
