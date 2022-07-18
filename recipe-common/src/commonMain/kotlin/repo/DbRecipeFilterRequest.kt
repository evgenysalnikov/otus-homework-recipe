package com.salnikoff.recipe.common.repo

import com.salnikoff.recipe.common.models.RecipeSearchTypes
import com.salnikoff.recipe.common.models.RecipeVisibility
import com.salnikoff.recipe.common.models.UserId

data class DbRecipeFilterRequest(
    val titleFilter: String = "",
    val descriptionFilter: String = "",
    val stepsFilter: String = "",
    val ownerId: UserId = UserId.NONE,
    val visibility: RecipeVisibility = RecipeVisibility.NONE,
    val searchTypes: Set<RecipeSearchTypes> = setOf(),
)
