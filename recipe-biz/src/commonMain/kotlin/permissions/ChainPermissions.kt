package com.salnikoff.recipe.biz.permissions

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.handlers.worker
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.CorState
import com.salnikoff.recipe.common.models.RecipeUserGroups
import com.salnikoff.recipe.common.models.RecipeUserPermissions

fun ICorChainDsl<RecipeContext>.chainPermissions(title: String, description: String) = worker {
    this.title = title
    this.description = description
    on { state == CorState.RUNNING }
    handle {
        val permissionsAdd: Set<RecipeUserPermissions> = principal.groups.map {
            when (it) {
                RecipeUserGroups.USER -> setOf<RecipeUserPermissions>(
                    RecipeUserPermissions.READ_OWN,
                    RecipeUserPermissions.READ_PUBLIC,
                    RecipeUserPermissions.CREATE_OWN,
                    RecipeUserPermissions.UPDATE_OWN,
                    RecipeUserPermissions.DELETE_OWN
                )
                RecipeUserGroups.MODERATOR -> setOf()
                RecipeUserGroups.ADMIN_RECIPE-> setOf(
                    RecipeUserPermissions.UPDATE_PUBLIC
                )
                RecipeUserGroups.TEST -> setOf()
                RecipeUserGroups.BAN_RECIPE -> setOf()
            }
        }.flatten().toSet()

        val permissionsDel: Set<RecipeUserPermissions> = principal.groups.map {
            when (it) {
                RecipeUserGroups.USER -> setOf()
                RecipeUserGroups.MODERATOR -> setOf()
                RecipeUserGroups.ADMIN_RECIPE-> setOf()
                RecipeUserGroups.TEST -> setOf()
                RecipeUserGroups.BAN_RECIPE -> setOf(
                    RecipeUserPermissions.UPDATE_OWN,
                    RecipeUserPermissions.CREATE_OWN
                )
            }
        }.flatten().toSet()
        chainPermissions.addAll(permissionsAdd)
        chainPermissions.removeAll(permissionsDel)
        println("PRINCIPAL: $principal")
        println("PERMISSIONS: $chainPermissions")
    }
}
