package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.backend.services.RecipeService
import com.salnikoff.recipe.common.models.RecipePrincipalModel
import com.salnikoff.recipe.logging.recipeLogger
import io.ktor.server.application.*
import io.ktor.server.routing.*

private val logger = recipeLogger(Route::v1recipe::class.java)

fun Route.v1recipe(recipeService: RecipeService, principalSupplier: ApplicationCall.() -> RecipePrincipalModel) {
    route("recipe") {
        post("create") {
            call.createRecipe(recipeService, call.principalSupplier(), logger)
        }
        post("read") {
            call.readRecipe(recipeService, call.principalSupplier(), logger)
        }
        post("update") {
            call.updateRecipe(recipeService, call.principalSupplier(), logger)
        }
        post("delete") {
            call.deleteRecipe(recipeService, call.principalSupplier(), logger)
        }
        post("search") {
            call.searchRecipe(recipeService, call.principalSupplier(), logger)
        }
    }
}
