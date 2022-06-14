package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.backend.services.RecipeService
import com.salnikoff.recipe.common.models.RecipeCommand
import io.ktor.server.application.*

suspend fun ApplicationCall.createRecipe(recipeService: RecipeService) =
    controllerHelperV1<RecipeApiRecipeCreateRequest, RecipeApiRecipeCreateResponse>(RecipeCommand.CREATE) {
        recipeService.createRecipe(this)
    }


suspend fun ApplicationCall.readRecipe(recipeService: RecipeService) {
    controllerHelperV1<RecipeApiRecipeReadRequest, RecipeApiRecipeReadResponse>(RecipeCommand.READ) {
        recipeService.readRecipe(this)
    }
}

suspend fun ApplicationCall.updateRecipe(recipeService: RecipeService) {
    controllerHelperV1<RecipeApiRecipeUpdateRequest, RecipeApiRecipeUpdateResponse>(RecipeCommand.UPDATE) {
        recipeService.updateRecipe(this)
    }
}

suspend fun ApplicationCall.deleteRecipe(recipeService: RecipeService) {
    controllerHelperV1<RecipeApiRecipeDeleteRequest, RecipeApiRecipeDeleteResponse>(RecipeCommand.DELETE) {
        recipeService.deleteRecipe(this)
    }
}

suspend fun ApplicationCall.searchRecipe(recipeService: RecipeService) {
    controllerHelperV1<RecipeApiRecipeSearchRequest, RecipeApiRecipeSearchResponse>(RecipeCommand.SEARCH) {
        recipeService.searchRecipe(this)
    }

}
