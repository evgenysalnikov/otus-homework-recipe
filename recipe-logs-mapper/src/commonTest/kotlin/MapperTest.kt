package com.salnikoff.recipe.logs.mapper

import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class MapperTest {

    @Test
    fun fromContext() {
        val context = RecipeContext(
            requestId = RecipeRequestId("1234"),
            command = RecipeCommand.CREATE,
            recipeResponse = Recipe(
                title = "title",
                description = "desc",
                requirements = listOf<RecipeRequirement>(
                    RecipeRequirement("req1"),
                    RecipeRequirement("req2")
                ),
                duration = 1.0.hours,
                ownerId = UserId("owner-123"),
                visibility = RecipeVisibility.VISIBLE_PUBLIC,
                steps = "some test steps"
            ),
            errors = mutableListOf(
                RecipeError(
                    code = "err",
                    group = "request",
                    field = "title",
                    message = "wrong title",
                )
            ),
            state = CorState.RUNNING,
        )

        val log = context.toLog("test-id")

        assertEquals("test-id", log.logId)
        assertEquals("recipe", log.source)
        assertEquals("1234", log.recipe?.requestId)
        assertEquals("VISIBLE_PUBLIC", log.recipe?.responseRecipe?.visibility)
        assertEquals("wrong title", log.errors?.firstOrNull()?.message)
        assertEquals("3600", log.recipe?.responseRecipe?.duration?.duration)
        assertEquals("SECONDS", log.recipe?.responseRecipe?.duration?.timeunit)
        assertEquals("req1", log.recipe?.responseRecipe?.requirements?.first().toString())
    }
}
