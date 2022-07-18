package com.salnikoff.recipe.biz.permissions

import com.salnikoff.recipe.common.models.RecipeCommand
import com.salnikoff.recipe.common.models.RecipePrincipalRelations
import com.salnikoff.recipe.common.models.RecipeUserPermissions

data class AccessTableConditions(
    val command: RecipeCommand,
    val permission: RecipeUserPermissions,
    val relation: RecipePrincipalRelations
)

val accessTable = mapOf(
    // Create
    AccessTableConditions(
        command = RecipeCommand.CREATE,
        permission = RecipeUserPermissions.CREATE_OWN,
        relation = RecipePrincipalRelations.NONE
    ) to true,

    // Read
    AccessTableConditions(
        command = RecipeCommand.READ,
        permission = RecipeUserPermissions.READ_OWN,
        relation = RecipePrincipalRelations.OWN
    ) to true,
    AccessTableConditions(
        command = RecipeCommand.READ,
        permission = RecipeUserPermissions.READ_PUBLIC,
        relation = RecipePrincipalRelations.PUBLIC
    ) to true,

    // Update
    AccessTableConditions(
        command = RecipeCommand.UPDATE,
        permission = RecipeUserPermissions.UPDATE_OWN,
        relation = RecipePrincipalRelations.OWN
    ) to true,

    // Delete
    AccessTableConditions(
        command = RecipeCommand.DELETE,
        permission = RecipeUserPermissions.DELETE_OWN,
        relation = RecipePrincipalRelations.OWN
    ) to true,

)
