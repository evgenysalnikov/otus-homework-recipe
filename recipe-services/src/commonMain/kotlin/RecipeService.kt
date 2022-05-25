package com.salnikoff.recipe.backend.services

import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.common.models.RecipeError
import com.salnikoff.recipe.common.models.RecipeId
import com.salnikoff.recipe.common.models.RecipeVisibility
import com.salnikoff.recipe.common.models.RecipeWorkMode
import com.salnikoff.recipe.common.stubs.RecipeStubs
import com.salnikoff.recipe.stubs.Pie

class RecipeService {
    fun createRecipe(context: RecipeContext): RecipeContext {
        val response = when (context.workMode) {
            RecipeWorkMode.PROD -> TODO()
            RecipeWorkMode.TEST -> context.recipeRequest
            RecipeWorkMode.STUB -> Pie.getModel()
        }

        return context.successResponse {
            recipeResponse = response
        }
    }

    fun readRecipe(context: RecipeContext, buildError: () -> RecipeError): RecipeContext {
        val requestedId = context.recipeRequest.id

        return when (context.stubCase) {
            RecipeStubs.SUCCESS -> context.successResponse {
                recipeResponse = Pie.getModel().apply { id = requestedId }
            }
            else -> context.errorResponse(buildError) {
                it.copy(field = "recipe.id", message = notFoundError(requestedId.asString()))
            }
        }
    }

    fun updateRecipe(context: RecipeContext, buildError: () -> RecipeError): RecipeContext = when (context.stubCase) {
        RecipeStubs.SUCCESS -> context.successResponse {
            recipeResponse = Pie.getModel {
                if (recipeRequest.visibility != RecipeVisibility.NONE) visibility = recipeRequest.visibility
                if (recipeRequest.id != RecipeId.NONE) id = recipeRequest.id
                if (recipeRequest.title.isNotEmpty()) title = recipeRequest.title
            }
        }
        else -> context.errorResponse(buildError) {
            it.copy(field = "recipe.id", message = notFoundError(context.recipeRequest.id.asString()))
        }
    }

    fun deleteRecipe(context: RecipeContext, buildError: () -> RecipeError): RecipeContext = when (context.stubCase) {
        RecipeStubs.SUCCESS -> context.successResponse {
            recipeResponse = Pie.getModel {
                id = context.recipeRequest.id
            }
        }
        else -> context.errorResponse(buildError) {
            it.copy(field = "recipe.id", message = notFoundError(context.recipeRequest.id.asString()))
        }
    }

    fun searchRecipe(context: RecipeContext, buildError: () -> RecipeError): RecipeContext {
        val filter = context.recipeFilterRequest

        val searchableString = filter.searchString

        return when (context.stubCase) {
            RecipeStubs.SUCCESS -> context.successResponse {
                recipesResponse.addAll(Pie.getModels())
            }
            else -> context.errorResponse(buildError) {
                it.copy(
                    message = "Nothing found by $searchableString"
                )
            }
        }
    }
}
