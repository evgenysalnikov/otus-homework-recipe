package com.salnikoff.recipe.common.models.step

import kotlin.time.Duration

data class RecipeStepBase(
    override var hold: Duration,
    override var description: String
) : IRecipeStep

