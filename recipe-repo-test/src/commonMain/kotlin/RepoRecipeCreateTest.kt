package com.salnikoff.recipe.repo.test

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.common.models.RecipeVisibility
import com.salnikoff.recipe.common.models.UserId
import com.salnikoff.recipe.common.repo.DbRecipeRequest
import com.salnikoff.recipe.common.repo.IRecipeRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.time.Duration.Companion.minutes

abstract class RepoRecipeCreateTest {
    abstract val repo: IRecipeRepository

    @Test
    fun createSuccess() {
        val result = runBlocking { repo.createRecipe(DbRecipeRequest(createObj)) }
        val expected = createObj.copy(id = result.result?.id ?: RecipeId.NONE)
        assertEquals(true, result.isSuccess)
        assertEquals(expected.title, result.result?.title)
        assertEquals(expected.description, result.result?.description)
        assertEquals(expected.ownerId, result.result?.ownerId)
        assertEquals(expected.steps, result.result?.steps)
        assertEquals(expected.visibility, result.result?.visibility)
        assertNotEquals(RecipeId.NONE, result.result?.id)
        assertEquals(emptyList(), result.errors)
    }

    companion object: BaseInitRecipes("create") {
        private val createObj = Recipe(
            title = "create recipe",
            description = "create recipe description",
            ownerId = UserId("some-owner"),
            duration = 3.minutes,
            steps = "create steps",
            visibility = RecipeVisibility.VISIBLE_TO_GROUP
        )

        override val initObjects: List<Recipe> = emptyList()
    }
}