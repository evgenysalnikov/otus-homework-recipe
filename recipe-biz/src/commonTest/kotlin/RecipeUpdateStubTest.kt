package com.salnikoff.recipe.biz

import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.stubs.RecipeStubs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeUpdateStubTest {

    private val processor = RecipeProcessor()
    val id = RecipeId("SomeRecipeId")
    val title = "Recipe title"
    val description = "Recipe description"
    val steps = "Step by step"
    val vilisibility = RecipeVisibility.VISIBLE_PUBLIC


    @Test
    fun update() = runTest {
        val ctx = RecipeContext(
            command = RecipeCommand.UPDATE,
            state = CorState.NONE,
            workMode = RecipeWorkMode.STUB,
            stubCase = RecipeStubs.SUCCESS,
            recipeRequest = Recipe(
                id = id,
                title = title,
                description = description,
                visibility = vilisibility,
                steps = steps,
                lock = RecipeLock("update-recipe-lock")
            )
        )

        processor.exec(ctx)
        assertEquals(id, ctx.recipeResponse.id)
        assertEquals("Pie", ctx.recipeResponse.title)
        assertEquals("pie description", ctx.recipeResponse.description)
        assertEquals(vilisibility, ctx.recipeResponse.visibility)
        assertEquals("success update", ctx.recipeResponse.steps)
        assertEquals("update-recipe-lock", ctx.recipeResponse.lock.asString())
    }

    @Test
    fun badTitle() = runTest {
        val ctx = RecipeContext(
            command = RecipeCommand.UPDATE,
            state = CorState.NONE,
            workMode = RecipeWorkMode.STUB,
            stubCase = RecipeStubs.BAD_TITLE,
            recipeRequest = Recipe(
                id = id,
                title = "",
                description = description,
                visibility = vilisibility,
                steps = steps,
                lock = RecipeLock("update-recipe-lock")
            )
        )

        processor.exec(ctx)
        val error = ctx.errors.first()
        assertEquals("title", error.field)
        assertEquals("validation", error.group)
    }

    @Test
    fun badDescription() = runTest {
        val ctx = RecipeContext(
            command = RecipeCommand.UPDATE,
            state = CorState.NONE,
            workMode = RecipeWorkMode.STUB,
            stubCase = RecipeStubs.BAD_DESCRIPTION,
            recipeRequest = Recipe(
                id = id,
                title = title,
                description = "",
                visibility = vilisibility,
                steps = steps,
                lock = RecipeLock("update-recipe-lock")
            )
        )

        processor.exec(ctx)
        val error = ctx.errors.first()
        assertEquals("description", error.field)
        assertEquals("validation", error.group)
    }

    @Test
    fun badNoCase() = runTest {
        val ctx = RecipeContext(
            command = RecipeCommand.UPDATE,
            state = CorState.NONE,
            workMode = RecipeWorkMode.STUB,
            stubCase = RecipeStubs.CANNOT_DELETE,
            recipeRequest = Recipe(
                id = id,
                title = title,
                description = description,
                visibility = vilisibility,
                steps = steps,
                lock = RecipeLock("update-recipe-lock")
            )
        )

        processor.exec(ctx)
        val error = ctx.errors.first()
        assertEquals("stub", error.field)
        assertEquals("validation", error.group)
    }
}
