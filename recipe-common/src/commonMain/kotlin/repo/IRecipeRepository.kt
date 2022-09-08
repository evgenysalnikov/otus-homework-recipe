package com.salnikoff.recipe.common.repo

interface IRecipeRepository {

    suspend fun createRecipe(rq: DbRecipeRequest): DbRecipeResponse

    suspend fun readRecipe(rq: DbRecipeIdRequest): DbRecipeResponse

    suspend fun updateRecipe(rq: DbRecipeRequest): DbRecipeResponse

    suspend fun deleteRecipe(rq: DbRecipeIdRequest): DbRecipeResponse

    suspend fun searchRecipe(rq: DbRecipeFilterRequest): DbRecipesResponse

    companion object {
        val NONE = object : IRecipeRepository {
            override suspend fun createRecipe(rq: DbRecipeRequest): DbRecipeResponse {
                TODO("wait")
            }

            override suspend fun readRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
                TODO("wait")
            }

            override suspend fun updateRecipe(rq: DbRecipeRequest): DbRecipeResponse {
                TODO("wait")
            }

            override suspend fun deleteRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
                TODO("wait")
            }

            override suspend fun searchRecipe(rq: DbRecipeFilterRequest): DbRecipesResponse {
                TODO("wait")
            }
        }
    }
}
