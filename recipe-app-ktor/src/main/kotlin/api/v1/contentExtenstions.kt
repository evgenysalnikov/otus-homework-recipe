package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.api.v1.models.RecipeApiResponseResult
import com.salnikoff.recipe.common.models.RecipeError

fun buildError() = RecipeError(
    field = "_", code = RecipeApiResponseResult.ERROR.value
)
