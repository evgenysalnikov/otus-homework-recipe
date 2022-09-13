package com.salnikoff.recipe.app.ktor.stubs

import com.salnikoff.recipe.app.ktor.auth.addAuth
import com.salnikoff.recipe.app.ktor.config.KtorAuthConfig
import com.salnikoff.recipe.app.ktor.module
import io.ktor.client.request.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthTest {
    @Test
    fun invalidAudience() = testApplication {
        application {
            module(authConfig = KtorAuthConfig.TEST)
        }

        val response = client.post("/v1/recipe/create") {
            addAuth(config = KtorAuthConfig.TEST.copy(audience = "invalid"))
        }
        assertEquals(401, response.status.value)
    }
}
