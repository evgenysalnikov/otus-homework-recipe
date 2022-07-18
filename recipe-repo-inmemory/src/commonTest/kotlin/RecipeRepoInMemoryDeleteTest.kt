package com.salnikoff.recipe.repo.inmemory

import com.salnikoff.recipe.common.repo.IRecipeRepository
import com.salnikoff.recipe.repo.test.RepoRecipeDeleteTest

class RecipeRepoInMemoryDeleteTest : RepoRecipeDeleteTest() {
    override val repo: IRecipeRepository = RecipeRepoInMemory(initObjects = initObjects)
}