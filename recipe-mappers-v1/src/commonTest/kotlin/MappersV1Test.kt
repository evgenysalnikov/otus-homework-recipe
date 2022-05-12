package com.salnikoff.recipe.mappers.v1

import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.models.step.RecipeStepBase
import com.salnikoff.recipe.common.models.step.RecipeStepWithImage
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
                steps = listOf(
                    RecipeApiStepBase(
                        hold = RecipeApiDuration(
                            duration = "1",
                            timeunit = RecipeApiTimeUnit.MIN
                        ),
                        description = "step 1"
                    ),
                    RecipeApiStepWithImage(
                        hold = RecipeApiDuration(
                            duration = "1",
                            timeunit = RecipeApiTimeUnit.MIN
                        ),
                        description = "step 2",
                        image = "https://example.com/test.png"
                    ),
                    RecipeApiStepBase(
                        hold = RecipeApiDuration(
                            duration = "1",
                            timeunit = RecipeApiTimeUnit.MIN
                        ),
                        description = "step 3"
                    )
                )
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
        assertEquals("step 1", context.recipeRequest.steps.first().description.toString())
        assertEquals("1m", context.recipeRequest.steps.first().hold.toString())
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
                steps = listOf(
                    RecipeApiStepBase(
                        hold = RecipeApiDuration(
                            duration = "1",
                            timeunit = RecipeApiTimeUnit.MIN
                        ),
                        description = "step 1"
                    ),
                    RecipeApiStepWithImage(
                        hold = RecipeApiDuration(
                            duration = "1",
                            timeunit = RecipeApiTimeUnit.MIN
                        ),
                        description = "step 1",
                        image = "https://example.com/test.png"
                    ),
                    RecipeApiStepBase(
                        hold = RecipeApiDuration(
                            duration = "1",
                            timeunit = RecipeApiTimeUnit.MIN
                        ),
                        description = "step 1"
                    )
                )
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
            recipesResponse = listOf(Recipe(
                id = RecipeId("my_recipe_id"),
                title = "Title",
                description = "description",
                requirements = listOf(
                    RecipeRequirement("req1"),
                    RecipeRequirement("req2"),
                ),
                duration = 3.0.minutes,
                ownerId = RecipeUserId("ownerId"),
                visibility = RecipeVisibility.VISIBLE_PUBLIC,
                steps = listOf(
                    RecipeStepBase(
                        hold = 1.0.minutes,
                        description = "step 1"
                    ),
                    RecipeStepWithImage(
                        hold = 1.0.minutes,
                        description = "step 2",
                        image = "https://example.com/test.png"
                    ),
                    RecipeStepBase(
                        hold = 1.0.minutes,
                        description = "step 1"
                    )
                )
            )),
            errors = mutableListOf(
                RecipeError(
                    code = "err",
                    group = "request",
                    field = "title",
                    message = "Wrong title"
                )
            ),
            state = RecipeState.RUNNING
        )

        val req = context.toTransport() as RecipeApiRecipeSearchResponse
        assertEquals("some_request_id", req.requestId)
        assertEquals("step 1", req.recipes?.first()?.steps?.first()?.description)
    }
}
