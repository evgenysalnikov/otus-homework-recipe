package com.salnikoff.recipe.mappers.v1.exceptions

import com.salnikoff.recipe.common.models.RecipeCommand

class UnknownRecipeCommand(command: RecipeCommand) : Throwable("Wrong command $command at mapping toTransport stage")
