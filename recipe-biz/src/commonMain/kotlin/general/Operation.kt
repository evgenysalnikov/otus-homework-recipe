package com.salnikoff.recipe.biz.general

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.chain
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeCommand

fun ICorChainDsl<RecipeContext>.operation(
    title:String,
    description: String,
    command: RecipeCommand,
    block: ICorChainDsl<RecipeContext>.() -> Unit
) = chain {
    block()
    this.title = title
    this.description = description
    on { this.command == command && this.state == CorState.RUNNING }
}
