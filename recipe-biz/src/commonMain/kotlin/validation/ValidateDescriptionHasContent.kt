package com.salnikoff.recipe.biz.validation

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.errorValidation
import com.salnikoff.recipe.common.models.fail

fun ICorChainDsl<RecipeContext>.validateDescriptionHasContent(title: String, description: String) = worker {
    this.title = title
    this.description = description
    val regExp = Regex("\\p{L}")
    on { recipeValidating.description.isNotEmpty() && !recipeValidating.description.contains(regExp)}
    handle {
        fail(errorValidation(
            field = "title",
            violationCode = "noContent",
            description = "field must contain letters"
        ))
    }
}
