package com.salnikoff.recipe.biz.repo

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.deepCopy

fun ICorChainDsl<RecipeContext>.repoPrepareUpdate(title: String) = worker {
    this.title = title
    description = "Готовим данные к сохранению в БД: совмещаем данные, прочитанные из БД, " +
            "и данные, полученные от пользователя"
    on { state == CorState.RUNNING }
    handle {
        recipeRepoPrepare = recipeRepoRead.deepCopy().apply {
            this.title = recipeValidated.title
            description = recipeValidated.description
            requirements = recipeValidated.requirements.deepCopy()
        }
    }
}
