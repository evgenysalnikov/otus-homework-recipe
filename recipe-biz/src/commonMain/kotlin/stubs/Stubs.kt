package com.salnikoff.recipe.biz.stubs

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.chain
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeWorkMode

fun ICorChainDsl<RecipeContext>.stubs(
    title: String,
    description: String,
    block: ICorChainDsl<RecipeContext>.() -> Unit
) = chain {
    block()
    this.title = title
    this.description = description
    on { this.workMode == RecipeWorkMode.STUB && state == CorState.RUNNING }
}
