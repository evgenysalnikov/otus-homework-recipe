package com.salnikoff.recipe.biz.validation

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.errorValidation
import com.salnikoff.recipe.common.models.fail

fun ICorChainDsl<RecipeContext>.validateTitleNotEmpty(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { recipeValidating.title.isEmpty() }
    handle {
        fail(errorValidation(
            field = "title",
            violationCode = "empty",
            description = "field must not be empty"
        ))
    }
}
