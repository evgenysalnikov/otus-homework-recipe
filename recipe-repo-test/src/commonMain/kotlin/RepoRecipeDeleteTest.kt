package com.salnikoff.recipe.repo.test

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.common.models.RecipeError
import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.common.models.RecipeLock
import com.salnikoff.recipe.common.repo.DbRecipeIdRequest
import com.salnikoff.recipe.common.repo.IRecipeRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class RepoRecipeDeleteTest {
    abstract val repo: IRecipeRepository
    protected open val successId = RecipeId(deleteSuccessStub.id.asString())

    @Test
    fun deleteSuccess() {
        val result = runBlocking { repo.deleteRecipe(DbRecipeIdRequest(deleteSuccessStub)) }
        assertEquals(true, result.isSuccess)
        assertEquals(emptyList(), result.errors)
        assertEquals(deleteSuccessStub.copy(id = successId), result.result)
    }

    @Test
    fun deleteNotFound() {
        val result = runBlocking { repo.deleteRecipe(DbRecipeIdRequest(id = notFoundId, lock = RecipeLock("my-test-lock"))) }

        assertEquals(false, result.isSuccess)
        assertEquals(null, result.result)
        assertEquals(
            listOf(RecipeError(field = "id", message = "Not Found")),
            result.errors
        )
    }

    companion object: BaseInitRecipes("delete") {
        override val initObjects: List<Recipe> = listOf(
            createInitTestModel("delete")
        )
        private val deleteSuccessStub = initObjects.first()
        val notFoundId = RecipeId("recipe-repo-delete-notFound")
    }
}