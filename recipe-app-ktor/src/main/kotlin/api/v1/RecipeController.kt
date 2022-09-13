package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.backend.services.RecipeService
import com.salnikoff.recipe.common.models.RecipeCommand
import com.salnikoff.recipe.common.models.RecipePrincipalModel
import io.ktor.server.application.*

suspend fun ApplicationCall.createRecipe(recipeService: RecipeService, principal: RecipePrincipalModel) =
    controllerHelperV1<RecipeApiRecipeCreateRequest, RecipeApiRecipeCreateResponse>(RecipeCommand.CREATE, principal) {
        recipeService.createRecipe(this)
    }


suspend fun ApplicationCall.readRecipe(recipeService: RecipeService, principal: RecipePrincipalModel) {
    controllerHelperV1<RecipeApiRecipeReadRequest, RecipeApiRecipeReadResponse>(RecipeCommand.READ, principal) {
        recipeService.readRecipe(this)
    }
}

suspend fun ApplicationCall.updateRecipe(recipeService: RecipeService, principal: RecipePrincipalModel) {
    controllerHelperV1<RecipeApiRecipeUpdateRequest, RecipeApiRecipeUpdateResponse>(RecipeCommand.UPDATE, principal) {
        recipeService.updateRecipe(this)
    }
}

suspend fun ApplicationCall.deleteRecipe(recipeService: RecipeService, principal: RecipePrincipalModel) {
    controllerHelperV1<RecipeApiRecipeDeleteRequest, RecipeApiRecipeDeleteResponse>(RecipeCommand.DELETE, principal) {
        recipeService.deleteRecipe(this)
    }
}

suspend fun ApplicationCall.searchRecipe(recipeService: RecipeService, principal: RecipePrincipalModel) {
    controllerHelperV1<RecipeApiRecipeSearchRequest, RecipeApiRecipeSearchResponse>(RecipeCommand.SEARCH, principal) {
        recipeService.searchRecipe(this)
    }

}
