package com.salnikoff.recipe.app.ktor.inmemory

import com.salnikoff.recipe.api.v1.apiV1ResponseDeserialize
import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.api.v1.recipeApiV1RequestSerialize
import com.salnikoff.recipe.app.ktor.auth.addAuth
import com.salnikoff.recipe.app.ktor.config.KtorAuthConfig
import com.salnikoff.recipe.app.ktor.module
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.repo.inmemory.RecipeRepoInMemory
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals

class V1RecipeTestApiTest {
    private val uuidOld = "10000000-0000-0000-0000-000000000001"
    private val uuidNew = "10000000-0000-0000-0000-000000000002"
    private val uuidSup = "10000000-0000-0000-0000-000000000003"

    private val initRecipe = Recipe(
        id = RecipeId(uuidOld),
        title = "abc",
        description = "abc",
        visibility = RecipeVisibility.VISIBLE_PUBLIC,
        lock = RecipeLock(uuidOld),
        ownerId = UserId("user1"),
    )
    private val initRecipeSupply = Recipe(
        id = RecipeId(uuidSup),
        title = "abc",
        description = "abc",
        visibility = RecipeVisibility.VISIBLE_PUBLIC,
        lock = RecipeLock(uuidSup),
    )


    @Test
    fun create() = testApplication {
        application {
            val repo by lazy { RecipeRepoInMemory(randomUuid = { uuidNew }) }
            val settings by lazy {
                RecipeSettings(repoProd = repo)
            }
            module(settings, authConfig = KtorAuthConfig.TEST)
        }

        val response = client.post("/v1/recipe/create") {
            val requestObj = RecipeApiRecipeCreateRequest(
                requestId = "12345",
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
        assertEquals(200, response.status.value)
        assertEquals(uuidNew, responseObj.recipe?.id)
        assertEquals(uuidNew, responseObj.recipe?.lock)
        assertEquals("Pie", responseObj.recipe?.title)
    }

    @Test
    fun read() = testApplication {
        application {
            val repo by lazy { RecipeRepoInMemory(initObjects = listOf(initRecipe), randomUuid = { uuidNew }) }
            val settings by lazy {
                RecipeSettings(repoProd = repo)
            }
            module(settings, authConfig = KtorAuthConfig.TEST)
        }
        val response = client.post("/v1/recipe/read") {
            val requestObj = RecipeApiRecipeReadRequest(
                requestId = "12345",
                recipe = RecipeApiBaseWithId(RecipeApiBaseWithIdRecipe(uuidOld))
            )
            contentType(ContentType.Application.Json)
            addAuth()
            setBody(recipeApiV1RequestSerialize(requestObj))
        }
        val responseObj = apiV1ResponseDeserialize<RecipeApiRecipeReadResponse>(response.bodyAsText())
        println(responseObj)
        assertEquals(200, response.status.value)
        assertEquals(uuidOld, responseObj.recipe?.id)
    }

    @Test
    fun update() = testApplication {
        application {
            val repo by lazy { RecipeRepoInMemory(initObjects = listOf(initRecipe), randomUuid = { uuidNew }) }
            val settings by lazy {
                RecipeSettings(repoProd = repo)
            }
            module(settings, authConfig = KtorAuthConfig.TEST)
        }
        val response = client.post("/v1/recipe/update") {
            val requestObj = RecipeApiRecipeUpdateRequest(
                requestId = "12345",
                recipe = RecipeApiRecipeUpdateWithLockObject(
                    id = uuidOld,
                    title = "Пирог",
                    description = "Острейший",
                    visibility = RecipeApiRecipeVisibility.PUBLIC,
                    lock = uuidOld,
                ),
            )
            contentType(ContentType.Application.Json)
            addAuth()
            setBody(recipeApiV1RequestSerialize(requestObj))
        }
        val responseObj = apiV1ResponseDeserialize<RecipeApiRecipeUpdateResponse>(response.bodyAsText())
        assertEquals(200, response.status.value)
        assertEquals(uuidOld, responseObj.recipe?.id)
    }

    @Test
    fun delete() = testApplication {
        application {
            val repo by lazy { RecipeRepoInMemory(initObjects = listOf(initRecipe), randomUuid = { uuidNew }) }
            val settings by lazy {
                RecipeSettings(repoProd = repo)
            }
            module(settings, authConfig = KtorAuthConfig.TEST)
        }
        val response = client.post("/v1/recipe/delete") {
            val requestObj = RecipeApiRecipeDeleteRequest(
                requestId = "12345",
                recipe = RecipeApiBaseWithIdAndLock(
                    RecipeApiBaseWithIdAndLockRecipe(
                        id = uuidOld,
                        lock = uuidNew,
                    )
                ),
            )
            contentType(ContentType.Application.Json)
            addAuth()
            setBody(recipeApiV1RequestSerialize(requestObj))
        }
        val responseObj = apiV1ResponseDeserialize<RecipeApiRecipeDeleteResponse>(response.bodyAsText())
        assertEquals(200, response.status.value)
        assertEquals(uuidOld, responseObj.recipe?.id)
        assertEquals(uuidOld, responseObj.recipe?.lock)
    }

    @Test
    fun search() = testApplication {
        application {
            val repo by lazy { RecipeRepoInMemory(initObjects = listOf(initRecipe), randomUuid = { uuidNew }) }
            val settings by lazy {
                RecipeSettings(repoProd = repo)
            }
            module(settings, authConfig = KtorAuthConfig.TEST)
        }
        val response = client.post("/v1/recipe/search") {
            val requestObj = RecipeApiRecipeSearchRequest(
                requestId = "12345",
                recipeFilter = RecipeApiRecipeSearchFilter(),
            )
            contentType(ContentType.Application.Json)
            addAuth()
            setBody(recipeApiV1RequestSerialize(requestObj))
        }
        val responseObj = apiV1ResponseDeserialize<RecipeApiRecipeSearchResponse>(response.bodyAsText())
        assertEquals(200, response.status.value)
        assertEquals(uuidOld, responseObj.recipes?.first()?.id)
    }
}
