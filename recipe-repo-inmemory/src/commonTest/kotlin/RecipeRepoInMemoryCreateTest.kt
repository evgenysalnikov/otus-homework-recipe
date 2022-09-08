package com.salnikoff.recipe.repo.inmemory

import com.salnikoff.recipe.common.repo.IRecipeRepository
import com.salnikoff.recipe.repo.test.RepoRecipeCreateTest

class RecipeRepoInMemoryCreateTest : RepoRecipeCreateTest() {
    override val repo: IRecipeRepository = RecipeRepoInMemory(initObjects = initObjects)
}