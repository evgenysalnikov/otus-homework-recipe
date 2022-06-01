package com.salnikoff.recipe.app.ktor.api

import com.salnikoff.recipe.app.ktor.api.v1.v1recipe
import com.salnikoff.recipe.backend.services.RecipeService
import io.ktor.server.routing.*

internal fun Routing.v1(recipeService: RecipeService) {
    route("v1") {
        v1recipe(recipeService)
    }
}
