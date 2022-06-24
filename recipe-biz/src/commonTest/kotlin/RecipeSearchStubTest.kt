package com.salnikoff.recipe.biz

import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.stubs.RecipeStubs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeSearchStubTest {

    private val processor = RecipeProcessor()
    val id = RecipeId("SomeRecipeId")
    val title = "Recipe title"
    val description = "Recipe description"
    val steps = "Step by step"
    val vilisibility = RecipeVisibility.VISIBLE_PUBLIC


    @Test
    fun read() = runTest {
        val ctx = RecipeContext(
            command = RecipeCommand.SEARCH,
            state = CorState.NONE,
            workMode = RecipeWorkMode.STUB,
            stubCase = RecipeStubs.SUCCESS,
            recipeFilterRequest = RecipeFilter(
                searchString = "foo"
            )
        )

        processor.exec(ctx)
        assertEquals(2, ctx.recipesResponse.size)
    }

    @Test
    fun badNoCase() = runTest {
        val ctx = RecipeContext(
            command = RecipeCommand.SEARCH,
            state = CorState.NONE,
            workMode = RecipeWorkMode.STUB,
            stubCase = RecipeStubs.BAD_TITLE,
            recipeFilterRequest = RecipeFilter(
                searchString = "foo"
            )
        )

        processor.exec(ctx)
        val error = ctx.errors.first()
        assertEquals("stub", error.field)
        assertEquals("validation", error.group)
    }
}
