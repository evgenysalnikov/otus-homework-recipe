package com.salnikoff.recipe.app.ktor.mappers

import com.salnikoff.recipe.common.models.RecipePrincipalModel
import com.salnikoff.recipe.common.models.RecipeUserGroups
import com.salnikoff.recipe.common.models.UserId
import io.ktor.server.auth.jwt.*

fun JWTPrincipal?.toModel() = this?.run {
    RecipePrincipalModel(
        id = payload.getClaim("id").asString()?.let { UserId(it) } ?: UserId.NONE,
        fname = payload.getClaim("fname").asString() ?: "",
        mname = payload.getClaim("mname").asString() ?: "",
        lname = payload.getClaim("lname").asString() ?: "",
        groups = payload
            .getClaim("groups")
            ?.asList(String::class.java)
            ?.mapNotNull {
                try {
                    RecipeUserGroups.valueOf(it)
                } catch (e: Throwable) {
                    null
                }
            }?.toSet() ?: emptySet()
    )
} ?: RecipePrincipalModel.NONE
