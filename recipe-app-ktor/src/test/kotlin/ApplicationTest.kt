package com.salnikoff.recipe.app.ktor

import com.salnikoff.recipe.app.ktor.config.KtorAuthConfig
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {
    @Test
    fun `root endpoint`() = testApplication {
        application { module(authConfig = KtorAuthConfig.TEST) }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, world!", bodyAsText())
        }
    }

}
