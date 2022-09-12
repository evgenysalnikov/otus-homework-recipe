package com.salnikoff.recipe.app.ktor.api.v1

import com.salnikoff.recipe.api.v1.models.*
import com.salnikoff.recipe.backend.services.RecipeService
import com.salnikoff.recipe.common.models.RecipeCommand
import com.salnikoff.recipe.common.models.RecipePrincipalModel
import com.salnikoff.recipe.logging.RecipeLogWrapper
import io.ktor.server.application.*

suspend fun ApplicationCall.createRecipe(
    recipeService: RecipeService,
    principal: RecipePrincipalModel,
    logger: RecipeLogWrapper
) =
    controllerHelperV1<RecipeApiRecipeCreateRequest, RecipeApiRecipeCreateResponse>(
        command = RecipeCommand.CREATE,
        principal = principal,
        logger = logger,
        logId = "recipe-create",
    ) {
        recipeService.createRecipe(this)
    }


suspend fun ApplicationCall.readRecipe(
    recipeService: RecipeService,
    principal: RecipePrincipalModel,
    logger: RecipeLogWrapper
) {
    controllerHelperV1<RecipeApiRecipeReadRequest, RecipeApiRecipeReadResponse>(
        command = RecipeCommand.READ,
        principal = principal,
        logger = logger,
        logId = "recipe-read",
    ) {
        recipeService.readRecipe(this)
    }
}

suspend fun ApplicationCall.updateRecipe(
    recipeService: RecipeService,
    principal: RecipePrincipalModel,
    logger: RecipeLogWrapper
) {
    controllerHelperV1<RecipeApiRecipeUpdateRequest, RecipeApiRecipeUpdateResponse>(
        command = RecipeCommand.UPDATE,
        principal = principal,
        logger = logger,
        logId = "recipe-update",
    ) {
        recipeService.updateRecipe(this)
    }
}

suspend fun ApplicationCall.deleteRecipe(
    recipeService: RecipeService,
    principal: RecipePrincipalModel,
    logger: RecipeLogWrapper
) {
    controllerHelperV1<RecipeApiRecipeDeleteRequest, RecipeApiRecipeDeleteResponse>(
        command = RecipeCommand.DELETE,
        principal = principal,
        logger = logger,
        logId = "recipe-delete",
    ) {
        recipeService.deleteRecipe(this)
    }
}

suspend fun ApplicationCall.searchRecipe(
    recipeService: RecipeService,
    principal: RecipePrincipalModel,
    logger: RecipeLogWrapper
) {
    controllerHelperV1<RecipeApiRecipeSearchRequest, RecipeApiRecipeSearchResponse>(
        command = RecipeCommand.SEARCH,
        principal = principal,
        logger = logger,
        logId = "recipe-search",
    ) {
        recipeService.searchRecipe(this)
    }

}
