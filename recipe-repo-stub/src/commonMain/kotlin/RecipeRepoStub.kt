package com.salnikoff.recipe.repo.stub

import com.salnikoff.recipe.common.repo.*
import com.salnikoff.recipe.stubs.Pie

class RecipeRepoStub() : IRecipeRepository {
    override suspend fun createRecipe(rq: DbRecipeRequest): DbRecipeResponse {
        return DbRecipeResponse(
            result = Pie.getModel(),
            isSuccess = true
        )
    }

    override suspend fun readRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
        return DbRecipeResponse(
            result = Pie.getModel(),
            isSuccess = true
        )
    }

    override suspend fun updateRecipe(rq: DbRecipeRequest): DbRecipeResponse {
        return DbRecipeResponse(
            result = Pie.getModel(),
            isSuccess = true
        )
    }

    override suspend fun deleteRecipe(rq: DbRecipeIdRequest): DbRecipeResponse {
        return DbRecipeResponse(
            result = Pie.getModel(),
            isSuccess = true
        )
    }

    override suspend fun searchRecipe(rq: DbRecipeFilterRequest): DbRecipesResponse {
        return DbRecipesResponse(
            result = Pie.getModels(),
            isSuccess = true
        )
    }

}