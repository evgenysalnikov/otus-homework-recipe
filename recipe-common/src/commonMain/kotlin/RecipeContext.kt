package com.salnikoff.recipe.common

import com.salnikoff.recipe.common.models.*
import com.salnikoff.recipe.common.repo.IRecipeRepository
import com.salnikoff.recipe.common.stubs.RecipeStubs
import kotlinx.datetime.Instant

data class RecipeContext(
    var command: RecipeCommand = RecipeCommand.NONE,
    var state: CorState = CorState.NONE,
    val errors: MutableList<RecipeError> = mutableListOf(),

    var workMode: RecipeWorkMode = RecipeWorkMode.PROD,
    var stubCase: RecipeStubs = RecipeStubs.NONE,

    var requestId: RecipeRequestId = RecipeRequestId.NONE,
    var timeStart: Instant = Instant.NONE,

    var recipeRequest: Recipe = Recipe(),
    var recipeFilterRequest: RecipeFilter = RecipeFilter(),

    var recipeValidating: Recipe = Recipe(),
    var recipeFilterValidating: RecipeFilter = RecipeFilter(),

    var recipeValidated: Recipe = Recipe(),
    var recipeFilterValidated: RecipeFilter = RecipeFilter(),

    var recipeResponse: Recipe = Recipe(),
    var recipesResponse: MutableList<Recipe> = mutableListOf(),

    var recipeRepo: IRecipeRepository = IRecipeRepository.NONE,
    var settings: RecipeSettings = RecipeSettings(),
    var principal: RecipePrincipalModel = RecipePrincipalModel.NONE,
    val chainPermissions: MutableSet<RecipeUserPermissions> = mutableSetOf(),
    var permitted: Boolean = false,
    var recipeRepoRead: Recipe = Recipe(),
    var recipeRepoPrepare: Recipe = Recipe(),
    var recipeRepoDone: Recipe = Recipe(),
    var recipesRepoDone: MutableList<Recipe> = mutableListOf(),
)
