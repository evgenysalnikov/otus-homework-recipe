package com.salnikoff.recipe.biz.general

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeWorkMode

fun ICorChainDsl<RecipeContext>.prepareResult(title: String) = worker {
    this.title = title
    description = "Подготовка данных для ответа клиенту на запрос"
    on { workMode != RecipeWorkMode.STUB }
    handle {
        recipeResponse = recipeRepoDone
        recipesResponse = recipesRepoDone
        state = when (val st = state) {
            CorState.RUNNING -> CorState.FINISHING
            else -> st
        }
    }
}
