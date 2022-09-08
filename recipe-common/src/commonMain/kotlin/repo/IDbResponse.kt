package com.salnikoff.recipe.common.repo

import com.salnikoff.recipe.common.models.RecipeError

interface IDbResponse<T> {
    val result: T?
    val isSuccess: Boolean
    val errors: List<RecipeError>
}
