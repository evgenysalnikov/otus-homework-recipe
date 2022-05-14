package com.salnikoff.recipe.common.models

import com.salnikoff.recipe.common.models.step.IRecipeStep
import kotlin.time.Duration

data class Recipe(
    var id: RecipeId = RecipeId.NONE,
    var title: String = "",
    var description: String = "",
    var requirements: List<RecipeRequirement> = listOf(),
    var duration: Duration = Duration.ZERO,
    var ownerId: RecipeUserId = RecipeUserId.NONE,
    var visibility: RecipeVisibility = RecipeVisibility.NONE,
    var steps: List<IRecipeStep> = listOf(),
    val permissionsClient: MutableSet<RecipePermissionClient> = mutableSetOf()
)
