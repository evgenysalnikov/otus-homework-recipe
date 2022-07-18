package com.salnikoff.recipe.biz.helpers

import com.salnikoff.recipe.common.models.RecipePrincipalModel
import com.salnikoff.recipe.common.models.RecipeUserGroups
import com.salnikoff.recipe.common.models.UserId
import com.salnikoff.recipe.stubs.Pie

fun principalUser(id: UserId = Pie.getModel().ownerId, banned: Boolean = false) = RecipePrincipalModel(
    id = id,
    groups = setOf(
        RecipeUserGroups.USER,
        RecipeUserGroups.TEST,
        if (banned) RecipeUserGroups.BAN_RECIPE else null
    )
        .filterNotNull()
        .toSet()
)

fun principalModer(id: UserId = Pie.getModel().ownerId, banned: Boolean = false) = RecipePrincipalModel(
    id = id,
    groups = setOf(
        RecipeUserGroups.MODERATOR_MP,
        RecipeUserGroups.TEST,
        if (banned) RecipeUserGroups.BAN_RECIPE else null
    )
        .filterNotNull()
        .toSet()
)

fun principalAdmin(id: UserId = Pie.getModel().ownerId, banned: Boolean = false) = RecipePrincipalModel(
    id = id,
    groups = setOf(
        RecipeUserGroups.ADMIN_RECIPE,
        RecipeUserGroups.TEST,
        if (banned) RecipeUserGroups.BAN_RECIPE else null
    )
        .filterNotNull()
        .toSet()
)
