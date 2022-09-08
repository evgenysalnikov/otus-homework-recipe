package com.salnikoff.recipe.biz.repo

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.repo.DbRecipeIdRequest

fun ICorChainDsl<RecipeContext>.repoDelete(title: String) = worker {
    this.title = title
    description = "Удаление объявления из БД по ID"
    on { state == CorState.RUNNING }
    handle {
        val request = DbRecipeIdRequest(recipeRepoPrepare)
        val result = recipeRepo.deleteRecipe(request)
        if (! result.isSuccess) {
            state = CorState.FAILING
            errors.addAll(result.errors)
        }
        recipeRepoDone = recipeRepoRead
    }
}
