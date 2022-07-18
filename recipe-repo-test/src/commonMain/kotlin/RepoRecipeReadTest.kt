package com.salnikoff.recipe.repo.test

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.common.models.RecipeError
import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.common.repo.DbRecipeIdRequest
import com.salnikoff.recipe.common.repo.IRecipeRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class RepoRecipeReadTest {
    abstract val repo: IRecipeRepository
    protected open val successId = Companion.successId

    @Test
    fun readSuccess() {
        val result = runBlocking { repo.readRecipe(DbRecipeIdRequest(successId)) }

        assertEquals(true, result.isSuccess)
        assertEquals(readSuccessStub.copy(successId), result.result)
        assertEquals(emptyList(), result.errors)
    }

    @Test
    fun readNotFound() {
        val result = runBlocking { repo.readRecipe(DbRecipeIdRequest(notFoundId)) }

        assertEquals(false, result.isSuccess)
        assertEquals(null, result.result)
        assertEquals(
            listOf(RecipeError(field = "id", message = "Not Found")),
            result.errors
        )
    }

    companion object : BaseInitRecipes("read") {
        override val initObjects: List<Recipe> = listOf(
            createInitTestModel("read")
        )
        private val readSuccessStub = initObjects.first()
        private val notFoundId = RecipeId("recipe-repo-read-notFound")
        private val successId = RecipeId(readSuccessStub.id.asString())
    }
}