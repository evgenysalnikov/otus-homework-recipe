package com.salnikoff.recipe.biz.permissions

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.chain
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*

fun ICorChainDsl<RecipeContext>.accessValidation(title: String, description: String) = chain {
    this.title = title
    this.description = "Вычисление прав доступа по группе принципала и таблице прав доступа"
    on { state == CorState.RUNNING }

    worker("Рецепт к принципалу","Вычисление отношения рецепта к принципалу") {
        recipeRepoRead.principalRelations = recipeRepoRead.resolveRelationsTo(principal)
    }

    worker("Доступ к рецепту", "Вычисление доступа к рецепту") {
        permitted = recipeRepoRead.principalRelations.asSequence().flatMap { relation ->
            chainPermissions.map { permission ->
                AccessTableConditions(
                    command = command,
                    permission = permission,
                    relation = relation,
                )
            }
        }.any {
            accessTable[it] ?: false
        }
    }

    worker {
        this.title = "Валидация прав доступа"
        this.description = "Проверка наличия прав для выполнения операции"
        on { !permitted }
        handle {
            fail(RecipeError(message = "User is not allowed to this operation"))
        }
    }
}

private fun Recipe.resolveRelationsTo(principal: RecipePrincipalModel): Set<RecipePrincipalRelations> = setOfNotNull(
    RecipePrincipalRelations.NONE,
    RecipePrincipalRelations.OWN.takeIf { principal.id == ownerId },
    RecipePrincipalRelations.PUBLIC.takeIf { visibility == RecipeVisibility.VISIBLE_PUBLIC },
    RecipePrincipalRelations.MODERATABLE.takeIf { visibility != RecipeVisibility.VISIBLE_TO_OWNER }

)
