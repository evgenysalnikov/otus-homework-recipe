package com.salnikoff.recipe.biz.validation

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*

fun ICorChainDsl<RecipeContext>.validateIdProperFormat(title: String, description: String) = worker {
    this.title = title
    this.description = description
    val regExp = Regex("^[0-9a-zA-Z-]+$")
    on { recipeValidating.id != RecipeId.NONE && !recipeValidating.id.asString().matches(regExp) }
    handle {
        val encodedId = recipeValidating.id.asString()
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        fail(
            errorValidation(
                field = "id",
                violationCode = "badFormat",
                description = "id must contain only numbers and letters"
            )
        )
    }
}
