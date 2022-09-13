package com.salnikoff.recipe.app.ktor.stubs

import com.salnikoff.recipe.api.v1.apiV1ResponseDeserialize
import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.api.v1.recipeApiV1RequestSerialize
import com.salnikoff.recipe.app.ktor.auth.addAuth
import com.salnikoff.recipe.app.ktor.config.KtorAuthConfig
import com.salnikoff.recipe.app.ktor.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class V1RecipeStubApiTest {
    @Test
    fun create() = testApplication {

        application {
            module(authConfig = KtorAuthConfig.TEST)
        }

        val response = client.post("/v1/recipe/create") {
            val requestObj = RecipeApiRecipeCreateRequest(
                requestId = "12345",
                debug = RecipeApiDebug(
                    mode = RecipeApiRequestDebugMode.STUB,
                    stub = RecipeApiRequestDebugStubs.SUCCESS
                ),
                recipe = RecipeApiRecipeCreateObject(
                    title = "Pie",
                    description = "Pie description",
                    visibility = RecipeApiRecipeVisibility.PUBLIC,
                    duration = RecipeApiDuration("3", RecipeApiTimeUnit.HOUR)
                ),
            )
            contentType(ContentType.Application.Json)
            addAuth()
            setBody(recipeApiV1RequestSerialize(requestObj))
        }

        val responseObj = apiV1ResponseDeserialize<RecipeApiRecipeCreateResponse>(response.bodyAsText())
        println(responseObj)
        assertEquals(200, response.status.value)
        assertEquals("create recipe biz", responseObj.recipe?.id)
    }

    @Test
    fun read() = testApplication {
        application { module(authConfig = KtorAuthConfig.TEST) }

        val response = client.post("/v1/recipe/read") {
            val requestObj = RecipeApiRecipeReadRequest(
                requestId = "12345",
                recipe = RecipeApiBaseWithId(RecipeApiBaseWithIdRecipe("readId")),
                debug = RecipeApiDebug(
                    mode = RecipeApiRequestDebugMode.STUB,
                    stub = RecipeApiRequestDebugStubs.SUCCESS
                )
            )
            contentType(ContentType.Application.Json)
            addAuth()
            setBody(recipeApiV1RequestSerialize(requestObj))
        }
        val responseObj = apiV1ResponseDeserialize<RecipeApiRecipeReadResponse>(response.bodyAsText())
        assertEquals(200, response.status.value)
        assertEquals("readId", responseObj.recipe?.id)
    }

    @Test
    fun update() = testApplication {
        application { module(authConfig = KtorAuthConfig.TEST) }

        val response = client.post("/v1/recipe/update") {
            val requestObj = RecipeApiRecipeUpdateRequest(
                requestId = "12345",
                recipe = RecipeApiRecipeUpdateWithLockObject(
                    id = "recipeUpdateId",
                    title = "Pie",
                    description = "Pie description",
                    visibility = RecipeApiRecipeVisibility.PUBLIC,
                ),
                debug = RecipeApiDebug(
                    mode = RecipeApiRequestDebugMode.STUB,
                    stub = RecipeApiRequestDebugStubs.SUCCESS
                )
            )
            contentType(ContentType.Application.Json)
            addAuth()
            setBody(recipeApiV1RequestSerialize(requestObj))
        }
        val responseObj = apiV1ResponseDeserialize<RecipeApiRecipeUpdateResponse>(response.bodyAsText())
        assertEquals(200, response.status.value)
        assertEquals("recipeUpdateId", responseObj.recipe?.id)
    }

    @Test
    fun delete() = testApplication {
        application { module(authConfig = KtorAuthConfig.TEST) }

        val response = client.post("/v1/recipe/delete") {
            val requestObj = RecipeApiRecipeDeleteRequest(
                requestId = "12345",
                recipe = RecipeApiBaseWithIdAndLock(
                    recipe = RecipeApiBaseWithIdAndLockRecipe(
                        id = "deleteRecipe"
                    ),
                ),
                debug = RecipeApiDebug(
                    mode = RecipeApiRequestDebugMode.STUB,
                    stub = RecipeApiRequestDebugStubs.SUCCESS
                )
            )
            contentType(ContentType.Application.Json)
            addAuth()
            setBody(recipeApiV1RequestSerialize(requestObj))
        }
        val responseObj = apiV1ResponseDeserialize<RecipeApiRecipeDeleteResponse>(response.bodyAsText())
        assertEquals(200, response.status.value)
        assertEquals("deleteRecipe", responseObj.recipe?.id)
    }

    @Test
    fun search() = testApplication {
        application { module(authConfig = KtorAuthConfig.TEST) }

        val response = client.post("/v1/recipe/search") {
            val requestObj = RecipeApiRecipeSearchRequest(
                requestId = "12345",
                recipeFilter = RecipeApiRecipeSearchFilter(),
                debug = RecipeApiDebug(
                    mode = RecipeApiRequestDebugMode.STUB,
                    stub = RecipeApiRequestDebugStubs.SUCCESS
                )
            )
            contentType(ContentType.Application.Json)
            addAuth()
            setBody(recipeApiV1RequestSerialize(requestObj))
        }
        val responseObj = apiV1ResponseDeserialize<RecipeApiRecipeSearchResponse>(response.bodyAsText())
        assertEquals(200, response.status.value)
        assertEquals("pie_recipe_id", responseObj.recipes?.first()?.id)
    }
}
