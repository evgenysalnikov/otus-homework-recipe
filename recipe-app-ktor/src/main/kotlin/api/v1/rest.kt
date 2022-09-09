package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.backend.services.RecipeService
import com.salnikoff.recipe.common.models.RecipePrincipalModel
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.v1recipe(recipeService: RecipeService, principalSupplier: ApplicationCall.() -> RecipePrincipalModel) {
    route("recipe") {
        post("create") {
            call.createRecipe(recipeService, call.principalSupplier())
        }
        post("read") {
            call.readRecipe(recipeService, call.principalSupplier())
        }
        post("update") {
            call.updateRecipe(recipeService, call.principalSupplier())
        }
        post("delete") {
            call.deleteRecipe(recipeService, call.principalSupplier())
        }
        post("search") {
            call.searchRecipe(recipeService, call.principalSupplier())
        }
    }
}
