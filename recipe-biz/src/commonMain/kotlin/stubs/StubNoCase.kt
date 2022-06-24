package com.salnikoff.recipe.biz.stubs

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeError

fun ICorChainDsl<RecipeContext>.stubNoCase(title:String, description: String) = worker {
    this.title = title
    this.description = description
    on { state == CorState.RUNNING }
    handle {
        state = CorState.FAILING
        errors.add(RecipeError(
            code = "validation",
            group = "validation",
            field = "stub",
            message = "Wrong stub case is requested: ${stubCase.name}"
        ))
    }
}
