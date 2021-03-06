package com.salnikoff.recipe.app.ktor

import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {
    @Test
    fun `root endpoint`() = testApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, world!", bodyAsText())
        }
    }

    @Test
    fun `recipe create v1`() = testApplication {
        client.post("/v1/recipe/create") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                """
                    {
                      "requestType": "create",
                      "requestId": "recipe_create_request_id",
                      "debug": {
                        "mode": "STUB",
                        "stub": "SUCCESS"
                      },
                      "recipe": {
                        "title": "string",
                        "description": "string",
                        "requirements": [
                          "string"
                        ],
                        "duration": {
                          "duration": "30.3",
                          "timeunit": "SEC"
                        },
                        "ownerId": "string",
                        "visibility": "OWNER_ONLY",
                        "steps": "string"
                      }
                    }
                """.trimIndent()
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "recipe_create_request_id")
            assertContains(bodyAsText(), "pie_recipe_id")
        }
    }

    @Test
    fun `recipe read v1`() = testApplication {
        client.post("/v1/recipe/read") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                """
                    {
                      "requestType": "read",
                      "requestId": "recipe_read_request_id",
                      "recipe": {
                        "recipe": {
                          "id": "read_id"
                        }
                      },
                      "debug": {
                        "stub": "SUCCESS",
                        "mode": "TEST"
                      }
                    }
                """.trimIndent()
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "recipe_read_request_id")
            assertContains(bodyAsText(), "read_id")
            assertContains(bodyAsText(), "pie description")
        }
    }

    @Test
    fun `recipe update v1`() = testApplication {
        client.post("/v1/recipe/update") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                """
                    {
                      "requestType": "update",
                      "requestId": "recipe_update_request_id",
                      "debug": {
                        "mode": "TEST",
                        "stub": "SUCCESS"
                      },
                      "recipe": {
                        "id":"updated_id",
                        "title": "string",
                        "description": "string",
                        "requirements": [
                          "string"
                        ],
                        "duration": {
                          "duration": "30.3",
                          "timeunit": "SEC"
                        },
                        "ownerId": "string",
                        "visibility": "OWNER_ONLY",
                        "steps": "string"
                      }
                    }
                """.trimIndent()
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "recipe_update_request_id")
            assertContains(bodyAsText(), "updated_id")
            assertContains(bodyAsText(), "pie description")
        }
    }

    @Test
    fun `recipe delete v1`() = testApplication {
        client.post("/v1/recipe/delete") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                """
                    {
                      "requestType": "delete",
                      "requestId": "recipe_delete_request_id",
                      "recipe": {
                        "recipe": {
                          "id": "delete_id"
                        }
                      },
                      "debug": {
                        "stub": "SUCCESS",
                        "mode": "TEST"
                      }
                    }
                """.trimIndent()
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "recipe_delete_request_id")
            assertContains(bodyAsText(), "delete_id")
            assertContains(bodyAsText(), "pie description")
        }
    }

    @Test
    fun `recipe search v1`() = testApplication {
        client.post("/v1/recipe/search") {
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                """
                    {
                      "requestType": "search",
                      "requestId": "recipe_search_request_id",
                      "debug": {
                        "mode": "TEST",
                        "stub": "SUCCESS"
                      },
                      "recipeFilter": {
                        "searchString": "foobarkeyword"
                      }
                    }
                """.trimIndent()
            )
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "recipe_search_request_id")
            assertContains(bodyAsText(), "foobarkeyword")
        }
    }
}
