package com.salnikoff.recipe.biz.permissions

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.chain
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeSearchTypes
import com.salnikoff.recipe.common.models.RecipeUserPermissions

fun ICorChainDsl<RecipeContext>.searchTypes(title: String) = chain {
    this.title = title
    description = "Добавление ограничений в поисковый запрос согласно правам доступа и др. политикам"
    on { state == CorState.RUNNING }
    worker("Определение типа поиска", "Определение типа поиска") {
        recipeFilterValidated.searchTypes = setOfNotNull(
            RecipeSearchTypes.OWN.takeIf { chainPermissions.contains(RecipeUserPermissions.SEARCH_OWN) },
            RecipeSearchTypes.PUBLIC.takeIf { chainPermissions.contains(RecipeUserPermissions.SEARCH_PUBLIC) },
            RecipeSearchTypes.REGISTERED.takeIf { chainPermissions.contains(RecipeUserPermissions.SEARCH_REGISTERED) },
        ).toMutableSet()
    }
}
