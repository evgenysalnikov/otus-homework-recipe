package com.salnikoff.recipe.app.ktor.config

import io.ktor.server.application.*

data class KtorAuthConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
) {
    companion object {
        const val ID_CLAIM = "id"
        const val GROUPS_CLAIM = "groups"
        const val F_NAME_CLAIM = "fname"
        const val M_NAME_CLAIM = "mname"
        const val L_NAME_CLAIM = "lname"

        val TEST = KtorAuthConfig(
            secret = "secret",
            issuer = "SalnikoffCom",
            audience = "recipe-users",
            realm = "Recipes"
        )
    }
}
