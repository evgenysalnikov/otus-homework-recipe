package com.salnikoff.recipe.common.models

import com.salnikoff.recipe.common.RecipeContext

data class RecipeError(
    val code: String = "",
    val group: String = "",
    val field: String = "",
    val message: String = "",
    val exception: Throwable? = null,
    val level: RecipeErrorLevels = RecipeErrorLevels.ERROR
)

fun Throwable.asRecipeError(
    code: String = "unknown",
    group: String = "exceptions",
    message: String = this.message ?: "",
) = RecipeError(
    code = code,
    group = group,
    field = "",
    message = message,
    exception = this
)

fun RecipeContext.addError(error: RecipeError) = errors.add(error)
fun RecipeContext.fail(error: RecipeError) {
    addError(error)
    state = CorState.FAILING
}
