package com.salnikoff.recipe.common.models

fun errorValidation(
    field: String,
    violationCode: String,
    description: String,
) = RecipeError(
    code = "validation-$field-$violationCode",
    field = field,
    group = "validation",
    message = "Validation error for field $field: $description",
)
