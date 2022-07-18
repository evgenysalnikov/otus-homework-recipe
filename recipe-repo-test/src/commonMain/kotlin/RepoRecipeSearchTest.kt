package com.salnikoff.recipe.repo.test

import com.salnikoff.recipe.common.models.Recipe
import com.salnikoff.recipe.common.models.UserId
import com.salnikoff.recipe.common.repo.DbRecipeFilterRequest
import com.salnikoff.recipe.common.repo.IRecipeRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class RepoRecipeSearchTest {
    abstract val repo: IRecipeRepository
    protected open val initRecipes: List<Recipe> = initObjects

    @Test
    fun searchSuccess() {
        val result = runBlocking { repo.searchRecipe(DbRecipeFilterRequest(ownerId = searchOwnerId)) }

        assertEquals(true, result.isSuccess)
        val expected = initRecipes.filter { it.ownerId == searchOwnerId }.sortedBy { it.id.asString() }
        assertEquals(expected, result.result?.sortedBy { it.id.asString() })
        assertEquals(emptyList(), result.errors)
    }

    companion object : BaseInitRecipes("search") {

        val searchOwnerId = UserId("test-search-owner")
        override val initObjects: List<Recipe> = listOf(
            createInitTestModel("search-1"),
            createInitTestModel("search-2", ownerId = searchOwnerId),
            createInitTestModel("search-3"),
            createInitTestModel("search-4", ownerId = searchOwnerId),
        )

    }
}
