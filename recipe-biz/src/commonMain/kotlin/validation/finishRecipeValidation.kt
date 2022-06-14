package com.salnikoff.recipe.biz.validation

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState

fun ICorChainDsl<RecipeContext>.finishRecipeValidation(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { state == CorState.RUNNING }
    handle {
        recipeValidated = recipeValidating
    }
}

fun ICorChainDsl<RecipeContext>.finishRecipeFilterValidation(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { state == CorState.RUNNING }
    handle {
        recipeFilterValidated = recipeFilterValidating
    }
}
