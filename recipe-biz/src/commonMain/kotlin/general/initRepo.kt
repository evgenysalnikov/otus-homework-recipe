package com.salnikoff.recipe.biz.general

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.RecipeWorkMode
import com.salnikoff.recipe.common.repo.IRecipeRepository

fun ICorChainDsl<RecipeContext>.initRepo(title: String, description: String) = worker {
    this.title = title
    this.description = "Инициализируем репозиторий в зависимости от режима работы приложения".trimIndent()
    handle {
        recipeRepo = when (workMode) {
            RecipeWorkMode.TEST -> settings.repoTest
            RecipeWorkMode.STUB -> IRecipeRepository.NONE
            else -> settings.repoProd
        }
    }
}
