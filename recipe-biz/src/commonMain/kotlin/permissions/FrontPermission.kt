package com.salnikoff.recipe.biz.permissions

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.chain
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipePermissionClient
import com.salnikoff.recipe.common.models.RecipeUserGroups

fun ICorChainDsl<RecipeContext>.frontPermissions(title: String) = chain {
    this.title = title
    description = "Вычисление разрешений пользователей для фронтенда"

    on { state == CorState.RUNNING }

    worker {
        this.title = "Разрешения для собственного объявления"
        description = this.title
        on { recipeRepoDone.ownerId == principal.id }
        handle {
            val permAdd: Set<RecipePermissionClient> = principal.groups.map {
                when (it) {
                    RecipeUserGroups.USER -> setOf(
                        RecipePermissionClient.READ,
                        RecipePermissionClient.UPDATE,
                        RecipePermissionClient.DELETE,
                    )
                    RecipeUserGroups.MODERATOR -> setOf()
                    RecipeUserGroups.ADMIN_RECIPE -> setOf()
                    RecipeUserGroups.TEST -> setOf()
                    RecipeUserGroups.BAN_RECIPE -> setOf()
                }
            }.flatten().toSet()
            val permDel: Set<RecipePermissionClient> = principal.groups.map {
                when (it) {
                    RecipeUserGroups.USER -> setOf()
                    RecipeUserGroups.MODERATOR -> setOf()
                    RecipeUserGroups.ADMIN_RECIPE -> setOf()
                    RecipeUserGroups.TEST -> setOf()
                    RecipeUserGroups.BAN_RECIPE -> setOf(
                        RecipePermissionClient.UPDATE,
                        RecipePermissionClient.DELETE,
                    )
                }
            }.flatten().toSet()
            recipeRepoDone.permissionsClient.addAll(permAdd)
            recipeRepoDone.permissionsClient.removeAll(permDel)
        }
    }

    worker {
        this.title = "Разрешения для модератора"
        description = this.title
        on { recipeRepoDone.ownerId != principal.id /* && tag, group, ... */ }
        handle {
            val permAdd: Set<RecipePermissionClient> = principal.groups.map {
                when (it) {
                    RecipeUserGroups.USER -> setOf()
                    RecipeUserGroups.MODERATOR -> setOf(
                        RecipePermissionClient.READ,
                        RecipePermissionClient.UPDATE,
                        RecipePermissionClient.DELETE,
                    )
                    RecipeUserGroups.ADMIN_RECIPE -> setOf()
                    RecipeUserGroups.TEST -> setOf()
                    RecipeUserGroups.BAN_RECIPE -> setOf()
                }
            }.flatten().toSet()
            val permDel: Set<RecipePermissionClient> = principal.groups.map {
                when (it) {
                    RecipeUserGroups.USER -> setOf(
                        RecipePermissionClient.UPDATE,
                        RecipePermissionClient.DELETE,
                    )
                    RecipeUserGroups.MODERATOR -> setOf()
                    RecipeUserGroups.ADMIN_RECIPE -> setOf()
                    RecipeUserGroups.TEST -> setOf()
                    RecipeUserGroups.BAN_RECIPE -> setOf(
                        RecipePermissionClient.UPDATE,
                        RecipePermissionClient.DELETE,
                    )
                }
            }.flatten().toSet()
            recipeRepoDone.permissionsClient.addAll(permAdd)
            recipeRepoDone.permissionsClient.removeAll(permDel)
        }
    }
    worker {
        this.title = "Разрешения для администратора"
        description = this.title
    }
}
