package com.salnikoff.recipe.repo.inmemory

import com.salnikoff.recipe.common.repo.IRecipeRepository
import com.salnikoff.recipe.repo.test.RepoRecipeReadTest

class RecipeRepoInMemoryReadTest : RepoRecipeReadTest() {
    override val repo: IRecipeRepository = RecipeRepoInMemory(initObjects = initObjects)
}
