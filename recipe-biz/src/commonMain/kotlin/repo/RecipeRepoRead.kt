package com.salnikoff.recipe.biz.repo

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.repo.DbRecipeIdRequest

fun ICorChainDsl<RecipeContext>.repoRead(title: String) = worker {
    this.title = title
    description = "Чтение объявления из БД"
    on { state == CorState.RUNNING }
    handle {
        val request = DbRecipeIdRequest(recipeValidated)
        val result = recipeRepo.readRecipe(request)
        val resultAd = result.result
        if (result.isSuccess && resultAd != null) {
            recipeRepoRead = resultAd
        } else {
            state = CorState.FAILING
            errors.addAll(result.errors)
        }
    }
}
