package com.salnikoff.recipe.biz.validation

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.errorValidation
import com.salnikoff.recipe.common.models.fail

fun ICorChainDsl<RecipeContext>.validateIdNotEmpty(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { recipeValidating.id.asString().isEmpty() }
    handle {
        fail(errorValidation(
            field = "id",
            violationCode = "empty",
            description = "field must not be empty"
        ))
    }
}
