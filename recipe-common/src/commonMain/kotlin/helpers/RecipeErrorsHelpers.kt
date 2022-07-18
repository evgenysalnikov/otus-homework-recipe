package com.salnikoff.recipe.common.helpers

import com.salnikoff.recipe.common.models.RecipeError
import com.salnikoff.recipe.common.models.RecipeErrorLevels

fun errorConcurrency(
    violationCode: String,
    description: String,
    level: RecipeErrorLevels = RecipeErrorLevels.ERROR
) = RecipeError(
    field = "lock",
    code = "concurrent-$violationCode",
    group = "concurrency",
    message = "Concurrent object access error: $description",
    level = level,
)

fun errorAdministration(
    field: String = "",
    violationCode: String,
    description: String,
    exception: Exception? = null,
    level: RecipeErrorLevels = RecipeErrorLevels.ERROR,
) = RecipeError(
    field = field,
    code = "administration-$violationCode",
    group = "administration",
    message = "Microservice management error: $description",
    level = level,
)
