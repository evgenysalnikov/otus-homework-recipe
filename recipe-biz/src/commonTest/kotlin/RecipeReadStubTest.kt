package com.salnikoff.recipe.biz

import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.stubs.RecipeStubs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeReadStubTest {

    private val processor = RecipeProcessor()
    val id = RecipeId("SomeRecipeId")
    val title = "Recipe title"
    val description = "Recipe description"
    val steps = "Step by step"
    val vilisibility = RecipeVisibility.VISIBLE_PUBLIC


    @Test
    fun read() = runTest {
        val ctx = RecipeContext(
            command = RecipeCommand.READ,
            state = CorState.NONE,
            workMode = RecipeWorkMode.STUB,
            stubCase = RecipeStubs.SUCCESS,
            recipeRequest = Recipe(
                id = id,
            )
        )

        processor.exec(ctx)
        println(ctx.recipeResponse)
        assertEquals(id, ctx.recipeResponse.id)
        assertEquals("Pie", ctx.recipeResponse.title)
        assertEquals("pie description", ctx.recipeResponse.description)
        assertEquals(vilisibility, ctx.recipeResponse.visibility)
        assertEquals("success read", ctx.recipeResponse.steps)
    }

    @Test
    fun badId() = runTest {
        val ctx = RecipeContext(
            command = RecipeCommand.READ,
            state = CorState.NONE,
            workMode = RecipeWorkMode.STUB,
            stubCase = RecipeStubs.BAD_ID,
            recipeRequest = Recipe()
        )

        processor.exec(ctx)
        val error = ctx.errors.first()
        assertEquals("id", error.field)
        assertEquals("validation", error.group)
    }

    @Test
    fun badNoCase() = runTest {
        val ctx = RecipeContext(
            command = RecipeCommand.READ,
            state = CorState.NONE,
            workMode = RecipeWorkMode.STUB,
            stubCase = RecipeStubs.BAD_TITLE,
            recipeRequest = Recipe(
                id = id,
                title = title,
                description = description,
                visibility = vilisibility,
                steps = steps
            )
        )

        processor.exec(ctx)
        val error = ctx.errors.first()
        assertEquals("stub", error.field)
        assertEquals("validation", error.group)
    }
}
