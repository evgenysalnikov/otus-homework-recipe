package com.salnikoff.recipe.repo.test

import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.repo.DbRecipeFilterRequest
import com.salnikoff.recipe.common.repo.DbRecipeRequest
import com.salnikoff.recipe.common.repo.IRecipeRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

abstract class RepoRecipeUpdateTest {
    abstract val repo: IRecipeRepository
    protected open val updateId = initObjects.first().id
    protected open val newLock = RecipeLock("30000-update-00000-1000")
    protected open val updateObj = Recipe(
        id = updateId,
        lock = initObjects.first().lock,
        title = "update title",
        description = "update description",
        requirements = listOf(RecipeRequirement("update-1"), RecipeRequirement("update-2")),
        duration = 1.hours,
        ownerId = UserId("owner-123"),
        steps = "update steps"
    )

    @Test
    fun updateSuccess() {
        val result = runBlocking { repo.updateRecipe(DbRecipeRequest(updateObj)) }

        assertEquals(true, result.isSuccess)
        assertEquals(updateObj.id, result.result?.id)
        assertEquals(updateObj.title, result.result?.title)
        assertEquals(updateObj.description, result.result?.description)
        assertEquals(updateObj.steps, result.result?.steps)
        assertEquals(updateObj.duration, result.result?.duration)
        assertEquals(newLock, result.result?.lock)
        assertEquals(emptyList(), result.errors)

    }

    @Test
    fun updateNotFound() {
        val result = runBlocking { repo.updateRecipe(DbRecipeRequest(updateObjNotFound)) }

        assertEquals(false, result.isSuccess)
        assertEquals(null, result.result)
        assertEquals(listOf(RecipeError(field = "id", message = "Not Found")), result.errors)

    }

    companion object : BaseInitRecipes("update") {
        override val initObjects: List<Recipe> = listOf(
            createInitTestModel("update"),
        )

        private val recipeUpdateIdNotFound = RecipeId("update-recipe-id-not-found")

        private val updateObjNotFound = Recipe(
            id = recipeUpdateIdNotFound,
            lock = initObjects.first().lock,
            title = "update title not found",
            description = "update description not found",
            requirements = listOf(RecipeRequirement("rnf-1"), RecipeRequirement("rnf-2")),
            duration = 5.hours,
            steps = "update steps not found"
        )
    }
}
