package com.salnikoff.recipe.common

import com.salnikoff.recipe.common.models.*
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
    var recipeResponse: Recipe = Recipe(),
    var recipesResponse: List<Recipe> = mutableListOf()
)
