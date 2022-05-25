package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.backend.services.RecipeService
import com.salnikoff.recipe.common.RecipeContext
import com.salnikoff.recipe.mappers.v1.fromTransport
import com.salnikoff.recipe.mappers.v1.toTransport
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

suspend fun ApplicationCall.createRecipe(recipeService: RecipeService) {
    val createRecipeRequest = receive<RecipeApiRecipeCreateRequest>()
    respond(
        RecipeContext().apply { fromTransport(createRecipeRequest) }.let {
            recipeService.createRecipe(it)
        }.toTransport()
    )
}

suspend fun ApplicationCall.readRecipe(recipeService: RecipeService) {
    val readRecipeRequest = receive<RecipeApiRecipeReadRequest>()
    respond(
        RecipeContext().apply { fromTransport(readRecipeRequest) }.let {
            recipeService.readRecipe(it, ::buildError)
        }.toTransport()
    )
}

suspend fun ApplicationCall.updateRecipe(recipeService: RecipeService) {
    val updateRecipeRequest = receive<RecipeApiRecipeUpdateRequest>()
    respond(
        RecipeContext().apply { fromTransport(updateRecipeRequest) }.let {
            recipeService.updateRecipe(it, ::buildError)
        }.toTransport()
    )
}

suspend fun ApplicationCall.deleteRecipe(recipeService: RecipeService) {
    val deleteRecipeRequest = receive<RecipeApiRecipeDeleteRequest>()
    respond(
        RecipeContext().apply { fromTransport(deleteRecipeRequest) }.let {
            recipeService.deleteRecipe(it, ::buildError)
        }.toTransport()
    )
}

suspend fun ApplicationCall.searchRecipe(recipeService: RecipeService) {
    val searchRecipeRequest = receive<RecipeApiRecipeSearchRequest>()
    respond(
        RecipeContext().apply { fromTransport(searchRecipeRequest) }.let {
            recipeService.searchRecipe(it, ::buildError)
        }.toTransport()
    )
}
