package com.salnikoff.recipe.common.models

import com.salnikoff.recipe.common.repo.IRecipeRepository

data class RecipeSettings(
    val repoStub: IRecipeRepository = IRecipeRepository.NONE,
    val repoTest: IRecipeRepository = IRecipeRepository.NONE,
    val repoProd: IRecipeRepository = IRecipeRepository.NONE,
)
