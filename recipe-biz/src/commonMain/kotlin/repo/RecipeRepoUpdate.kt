package com.salnikoff.recipe.biz.repo

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.repo.DbRecipeRequest

fun ICorChainDsl<RecipeContext>.repoUpdate(title: String) = worker {
    this.title = title
    description = "Обновление рецепта в базе данных"
    on { state == CorState.RUNNING }
    handle {
        val request = DbRecipeRequest(
            recipeRepoPrepare.deepCopy().apply {
                this.title = recipeValidated.title
                description = recipeValidated.description
                visibility = recipeValidated.visibility
            }
        )
        val result = recipeRepo.updateRecipe(request)
        val resultRecipe = result.result
        if (result.isSuccess && resultRecipe != null) {
            recipeRepoDone = resultRecipe
        } else {
            state = CorState.FAILING
            errors.addAll(result.errors)
            recipeRepoDone
        }
    }
}
