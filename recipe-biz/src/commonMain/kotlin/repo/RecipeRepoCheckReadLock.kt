package com.salnikoff.recipe.biz.repo

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.helpers.errorConcurrency
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.fail

fun ICorChainDsl<RecipeContext>.repoCheckReadLock(title: String) = worker {
    this.title = title
    description = """
        Проверяем, что блокировка из запроса совпадает с блокировкой в БД
    """.trimIndent()
    on { state == CorState.RUNNING && recipeValidated.lock != recipeRepoRead.lock }
    handle {
        fail(errorConcurrency(violationCode = "changed", "Object has been inconsistently changed"))
        recipeRepoDone = recipeRepoRead
    }
}
