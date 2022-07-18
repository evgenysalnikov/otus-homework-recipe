package com.salnikoff.recipe.biz.repo

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.repo.DbRecipeFilterRequest
import com.salnikoff.recipe.common.repo.DbRecipeIdRequest

fun ICorChainDsl<RecipeContext>.repoSearch(title: String) = worker {
    this.title = title
    description = "Поиск рецептов в БД по фильтру"
    on { state == CorState.RUNNING }
    handle {
        val request = DbRecipeFilterRequest(
            titleFilter = recipeFilterValidated.searchString,
            ownerId = recipeFilterValidated.owner,
            searchTypes = recipeFilterValidated.searchTypes.toSet(),
        )
        val result = recipeRepo.searchRecipe(request)
        val resultAds = result.result
        if (result.isSuccess && resultAds != null) {
            recipesRepoDone = resultAds.toMutableList()
        } else {
            state = CorState.FAILING
            errors.addAll(result.errors)
        }
    }
}
