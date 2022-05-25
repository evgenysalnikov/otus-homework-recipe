package com.salnikoff.recipe.common.models

data class RecipeFilter(
    var searchString: String = "",
    var owner: UserId = UserId.NONE,
)
