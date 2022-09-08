package com.salnikoff.recipe.biz.repo

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.repo.DbRecipeRequest

fun ICorChainDsl<RecipeContext>.repoCreate(title: String) = worker {
    this.title = title
    description = "Добавление рецепта в БД"
    on { state == CorState.RUNNING }
    handle {
        val request = DbRecipeRequest(recipeRepoPrepare)
        val result = recipeRepo.createRecipe(request)
        val resultRecipe = result.result
        if (result.isSuccess && resultRecipe != null) {
            recipeRepoDone = resultRecipe
        } else {
            state = CorState.FAILING
            errors.addAll(result.errors)
        }
    }
}
