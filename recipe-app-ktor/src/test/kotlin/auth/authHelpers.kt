package com.salnikoff.recipe.app.ktor.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.salnikoff.recipe.app.ktor.config.KtorAuthConfig
import io.ktor.client.request.*
import io.ktor.http.*

fun HttpRequestBuilder.addAuth(
    id: String = "user1",
    groups: List<String> = listOf("USER"),
    config: KtorAuthConfig = KtorAuthConfig.TEST
) {
    val token = JWT.create()
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .withClaim(KtorAuthConfig.GROUPS_CLAIM, groups)
        .withClaim(KtorAuthConfig.ID_CLAIM, id)
        .sign(Algorithm.HMAC256(config.secret))

    header(HttpHeaders.Authorization, "Bearer $token")
}
