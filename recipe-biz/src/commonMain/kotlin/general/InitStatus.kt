package com.salnikoff.recipe.biz.general

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState

fun ICorChainDsl<RecipeContext>.initStatus(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { state == CorState.NONE }
    handle { state = CorState.RUNNING }
}
