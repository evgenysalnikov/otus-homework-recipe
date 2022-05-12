package com.salnikoff.recipe.common.models.step

import kotlin.time.Duration

sealed interface IRecipeStep {
    var hold: Duration
    var description: String
}
