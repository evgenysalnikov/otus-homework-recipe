package com.salnikoff.recipe.biz.stubs

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeError
import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.common.stubs.RecipeStubs
import com.salnikoff.recipe.stubs.Pie

fun ICorChainDsl<RecipeContext>.stubValidationBadTitle(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { stubCase == RecipeStubs.BAD_TITLE && state == CorState.RUNNING }
    handle {
        state = CorState.FAILING

        this.errors.add(
            RecipeError(
                code = "validation-title",
                group = "validation",
                field = "title",
                message = "Wrong title field"
            )
        )
        val stub = Pie.getModel() {
            id = RecipeId("create recipe biz")
            steps = "Bad title response steps"
        }

        recipeResponse = stub
    }
}
