package com.salnikoff.recipe.biz.repo

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState

fun ICorChainDsl<RecipeContext>.repoPrepareDelete(title: String) = worker {
    this.title = title
    description = """
        Готовим данные к удалению из БД
    """.trimIndent()
    on { state == CorState.RUNNING }
    handle {
        recipeRepoPrepare = recipeValidated.deepCopy()
    }
}
