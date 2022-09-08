package com.salnikoff.recipe.biz.stubs

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeLock
import com.salnikoff.recipe.common.stubs.RecipeStubs
import com.salnikoff.recipe.stubs.Pie

fun ICorChainDsl<RecipeContext>.stubReadSuccess(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { stubCase == RecipeStubs.SUCCESS && state == CorState.RUNNING }
    handle {
        state = CorState.FINISHING

        val stub = Pie.getModel() {
            id = recipeRequest.id
            steps = "success read"
            lock = RecipeLock("read-success-lock")
        }

        recipeResponse = stub
    }
}
