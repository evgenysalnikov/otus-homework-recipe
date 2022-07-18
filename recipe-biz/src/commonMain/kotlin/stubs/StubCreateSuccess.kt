package com.salnikoff.recipe.biz.stubs

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.common.models.RecipeLock
import com.salnikoff.recipe.common.stubs.RecipeStubs
import com.salnikoff.recipe.stubs.Pie

fun ICorChainDsl<RecipeContext>.stubCreateSuccess(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { stubCase == RecipeStubs.SUCCESS && state == CorState.RUNNING }
    handle {
        state = CorState.FINISHING

        val stub = Pie.getModel() {
            id = RecipeId("create recipe biz")
            steps = "success create"
            lock = RecipeLock("update-recipe-lock")
        }

        recipeResponse = stub
    }
}
