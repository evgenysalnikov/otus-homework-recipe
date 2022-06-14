package com.salnikoff.recipe.mappers.v1

import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class MappersV1Test {

    @Test
    fun fromTransportRecipeApiCreateRequestTest() {
        val recipeCreateRequest = RecipeApiRecipeCreateRequest(
            requestId = "my-create-recipe-request-id",
            recipe = RecipeApiRecipeCreateObject(
                title = "Title",
                description = "description",
                requirements = listOf(
                    "req1", "req2"
                ),
                duration = RecipeApiDuration(
                    duration = "3.5",
                    timeunit = RecipeApiTimeUnit.MIN
                ),
                ownerId = "ownerId",
                visibility = RecipeApiRecipeVisibility.PUBLIC,
                steps = "My recipe description. How to do it and so on"
            ),
            debug = RecipeApiDebug(
                mode = RecipeApiRequestDebugMode.TEST,
                stub = RecipeApiRequestDebugStubs.BAD_ID
            )
        )

        val context = RecipeContext()
        context.fromTransport(recipeCreateRequest)

        assertEquals("CREATE", context.command.toString())
        assertEquals("my-create-recipe-request-id", context.requestId.asString())
        assertEquals("req1", context.recipeRequest.requirements.first().asString())
        assertEquals("3m 30s", context.recipeRequest.duration.toString())
        assertEquals("ownerId", context.recipeRequest.ownerId.asString())
        assertEquals("VISIBLE_PUBLIC", context.recipeRequest.visibility.toString())
        assertEquals("TEST", context.workMode.toString())
        assertEquals("BAD_ID", context.stubCase.toString())
        assertEquals("My recipe description. How to do it and so on", context.recipeRequest.steps)
    }

    @Test
    fun fromTransportRecipeApiReadRequestTest() {
        val recipeReadRequest = RecipeApiRecipeReadRequest(
            requestId = "my_request_id",
            recipe = RecipeApiBaseWithId(
                recipe = RecipeApiBaseWithIdRecipe("recipe_id")
            ),
            debug = RecipeApiDebug(
                mode = RecipeApiRequestDebugMode.PROD,
                stub = RecipeApiRequestDebugStubs.BAD_ID
            )
        )

        val context = RecipeContext()
        context.fromTransport(recipeReadRequest)
        assertEquals("READ", context.command.toString())

    }

    @Test
    fun fromTransportRecipeApiUpdateRequestTest() {
        val recipeUpdateRequest = RecipeApiRecipeUpdateRequest(
            requestId = "my_request_id",
            recipe = RecipeApiRecipeUpdateObject(
                id = "updated_recipe_id",
                title = "Title",
                description = "description",
                requirements = listOf(
                    "req1", "req2"
                ),
                duration = RecipeApiDuration(
                    duration = "3",
                    timeunit = RecipeApiTimeUnit.MIN
                ),
                ownerId = "ownerId",
                visibility = RecipeApiRecipeVisibility.PUBLIC,
                steps = "My recipe description. How to do it and so on"
            ),
            debug = RecipeApiDebug(
                mode = RecipeApiRequestDebugMode.PROD,
                stub = RecipeApiRequestDebugStubs.BAD_ID
            )
        )

        val context = RecipeContext()
        context.fromTransport(recipeUpdateRequest)
        assertEquals("UPDATE", context.command.toString())
    }

    @Test
    fun fromTransportRecipeApiDeleteRequestTest() {
        val recipeDeleteRequest = RecipeApiRecipeDeleteRequest(
            requestId = "my_delete_request",
            recipe = RecipeApiBaseWithId(
                recipe = RecipeApiBaseWithIdRecipe("recipe_id")
            ),
            debug = RecipeApiDebug(
                mode = RecipeApiRequestDebugMode.PROD,
                stub = RecipeApiRequestDebugStubs.BAD_ID
            )
        )

        val context = RecipeContext()
        context.fromTransport(recipeDeleteRequest)
        assertEquals("DELETE", context.command.toString())
    }

    @Test
    fun toTransportRecipeApiSearchResponseTest() {
        val context = RecipeContext(
            requestId = RecipeRequestId("some_request_id"),
            command = RecipeCommand.SEARCH,
            recipesResponse = mutableListOf(Recipe(
                id = RecipeId("my_recipe_id"),
                title = "Title",
                description = "description",
                requirements = listOf(
                    RecipeRequirement("req1"),
                    RecipeRequirement("req2"),
                ),
                duration = 3.0.minutes,
                ownerId = UserId("ownerId"),
                visibility = RecipeVisibility.VISIBLE_PUBLIC,
                steps = "My recipe description. How to do it and so on"
            )),
            errors = mutableListOf(
                RecipeError(
                    code = "err",
                    group = "request",
                    field = "title",
                    message = "Wrong title"
                )
            ),
            state = CorState.RUNNING
        )

        val req = context.toTransport() as RecipeApiRecipeSearchResponse
        assertEquals("some_request_id", req.requestId)
        assertEquals("My recipe description. How to do it and so on", req.recipes?.first()?.steps)
    }
}
