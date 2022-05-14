package com.salnikoff.recipe.common.models.step

import kotlin.time.Duration

data class RecipeStepWithImage(
    override var hold: Duration,
    override var description: String,
    var image: String = ""
) : IRecipeStep

