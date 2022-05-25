package com.salnikoff.recipe.common.models

import kotlin.time.Duration

data class Recipe(
    var id: RecipeId = RecipeId.NONE,
    var title: String = "",
    var description: String = "",
    var requirements: List<RecipeRequirement> = listOf(),
    var duration: Duration = Duration.ZERO,
    var ownerId: UserId = UserId.NONE,
    var visibility: RecipeVisibility = RecipeVisibility.NONE,
    var steps: String = "",
    val permissionsClient: MutableSet<RecipePermissionClient> = mutableSetOf()
)
