package com.salnikoff.recipe.repo.inmemory

import com.salnikoff.recipe.common.repo.IRecipeRepository
import com.salnikoff.recipe.repo.test.RepoRecipeSearchTest

class RecipeRepoInMemorySearchTest : RepoRecipeSearchTest() {
    override val repo: IRecipeRepository = RecipeRepoInMemory(initObjects = initObjects)
}
