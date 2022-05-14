package com.salnikoff.recipe.mappers.v1.exceptions

import com.salnikoff.recipe.api.v1.models.IRecipeApiStep

class UnknownRecipeStep(step: IRecipeApiStep) : Throwable("Cannot map unknown step $step")
