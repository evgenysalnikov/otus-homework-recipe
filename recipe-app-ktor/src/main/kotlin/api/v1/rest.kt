package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.backend.services.RecipeService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.v1recipe(recipeService: RecipeService) {
    route("recipe") {
        post("create") {
            call.createRecipe(recipeService)
        }
        post("read") {
            call.readRecipe(recipeService)
        }
        post("update") {
            call.updateRecipe(recipeService)
        }
        post("delete") {
            call.deleteRecipe(recipeService)
        }
        post("search") {
            call.searchRecipe(recipeService)
        }
    }
}
