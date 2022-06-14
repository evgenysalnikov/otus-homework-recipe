package com.salnikoff.recipe.biz.stubs

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeError
import com.salnikoff.recipe.common.stubs.RecipeStubs
import com.salnikoff.recipe.stubs.Pie

fun ICorChainDsl<RecipeContext>.stubValidationBadId(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { stubCase == RecipeStubs.BAD_ID && state == CorState.RUNNING }
    handle {
        state = CorState.FAILING

        this.errors.add(
            RecipeError(
                code = "validation-id",
                group = "validation",
                field = "id",
                message = "Wrong id field"
            )
        )
        val stub = Pie.getModel() {
            id = recipeRequest.id
        }

        recipeResponse = stub
    }
}
